package com.startingblue.fourtooncookie.aws.s3;

import com.startingblue.fourtooncookie.aws.s3.exception.S3Exception;
import com.startingblue.fourtooncookie.aws.s3.exception.S3PathNotFoundException;
import com.startingblue.fourtooncookie.aws.s3.exception.S3PreSignUrlException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.diaryimage.presignedurl.duration}")
    private Integer preSignedUrlDurationInMinutes;

    public URL generatePresignedUrl(String bucketName, String keyName) {
        verifyPathExists(bucketName, keyName);
        return createPresignedUrl(bucketName, keyName);
    }

    private void verifyPathExists(String bucketName, String keyName) {
        if (!isPathExists(bucketName, keyName)) {
            throw new S3PathNotFoundException(String.format("S3에 이미지가 존재하지 않습니다. Key: %s", keyName));
        }
    }

    private URL createPresignedUrl(String bucketName, String keyName) {
        try {
            GetObjectPresignRequest getObjectPresignRequest = createGetObjectPresignRequest(bucketName, keyName);
            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            return presignedGetObjectRequest.url();
        } catch (Exception e) {
            throw new S3PreSignUrlException(String.format("S3에서 프리사인 URL 생성 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    private boolean isPathExists(String bucketName, String keyName) {
        try {
            HeadObjectRequest headObjectRequest = createHeadObjectRequest(bucketName, keyName);
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return handleNoSuchKey(e);
        } catch (Exception e) {
            throw new S3Exception(String.format("S3에 이미지 존재 여부 확인 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    private HeadObjectRequest createHeadObjectRequest(String bucketName, String keyName) {
        return HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
    }

    private boolean handleNoSuchKey(NoSuchKeyException e) {
        if (e.statusCode() == 404) {
            return false;
        }
        throw new S3Exception("S3에 이미지 존재 여부 확인 중 오류가 발생했습니다.", e);
    }

    private GetObjectPresignRequest createGetObjectPresignRequest(String bucketName, String keyName) {
        return GetObjectPresignRequest.builder()
                .getObjectRequest(r -> r.bucket(bucketName).key(keyName))
                .signatureDuration(Duration.ofMinutes(preSignedUrlDurationInMinutes))
                .build();
    }

    public byte[] getImageFromS3(String bucketName, String keyName) {
        try {
            return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build()).asByteArray();
        } catch (NoSuchBucketException e) {
            throw new S3PathNotFoundException("S3에 버킷이 존재하지 않습니다.");
        } catch (NoSuchKeyException e) {
            throw new S3PathNotFoundException(String.format("S3에 키가 존재하지 않습니다. Key: %s", keyName));
        } catch (Exception e) {
            throw new S3Exception(String.format("S3에서 이미지 다운로드 중 오류가 발생했습니다. Key: %s", keyName), e);
        }
    }

    public void deleteObjectsInFolder(String bucketName, String folderKey) {
        try {
            if (!folderKey.endsWith("/")) {
                folderKey += "/";
            }

            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folderKey)
                    .build();

            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            List<S3Object> objectList = listObjectsResponse.contents();

            List<ObjectIdentifier> objectIdentifiers = objectList.stream()
                    .map(s3Object -> ObjectIdentifier.builder()
                            .key(s3Object.key())
                            .build())
                    .collect(Collectors.toList());

            if (!objectIdentifiers.isEmpty()) {
                DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder().objects(objectIdentifiers).build())
                        .build();

                s3Client.deleteObjects(deleteObjectsRequest);
            }
        } catch (NoSuchBucketException e) {
            throw new S3PathNotFoundException("S3에 버킷이 존재하지 않습니다.");
        } catch (NoSuchKeyException e) {
            throw new S3PathNotFoundException(String.format("S3에 키가 존재하지 않습니다. Key: %s", folderKey));
        } catch (Exception e) {
            throw new S3Exception(String.format("S3에서 삭제 중 오류가 발생했습니다. Key: %s", folderKey), e);
        }
    }
}
