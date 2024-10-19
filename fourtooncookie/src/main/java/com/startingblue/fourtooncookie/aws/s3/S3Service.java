package com.startingblue.fourtooncookie.aws.s3;

import com.startingblue.fourtooncookie.aws.s3.exception.S3Exception;
import com.startingblue.fourtooncookie.aws.s3.exception.S3PathNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;

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
