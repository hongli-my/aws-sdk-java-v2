/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.services.s3.internal.crt;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketAnalyticsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketIntelligentTieringConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketInventoryConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketLifecycleRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketLifecycleResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketMetricsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketOwnershipControlsResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketReplicationResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.DeleteBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketWebsiteResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletePublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.DeletePublicAccessBlockResponse;
import software.amazon.awssdk.services.s3.model.GetBucketAccelerateConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAccelerateConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;
import software.amazon.awssdk.services.s3.model.GetBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAnalyticsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.GetBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.GetBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketIntelligentTieringConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketInventoryConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLifecycleConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLocationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLocationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketLoggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketLoggingResponse;
import software.amazon.awssdk.services.s3.model.GetBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketMetricsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.GetBucketOwnershipControlsResponse;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyStatusRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyStatusResponse;
import software.amazon.awssdk.services.s3.model.GetBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketReplicationResponse;
import software.amazon.awssdk.services.s3.model.GetBucketRequestPaymentRequest;
import software.amazon.awssdk.services.s3.model.GetBucketRequestPaymentResponse;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningResponse;
import software.amazon.awssdk.services.s3.model.GetBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.GetBucketWebsiteResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAclResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectLegalHoldRequest;
import software.amazon.awssdk.services.s3.model.GetObjectLegalHoldResponse;
import software.amazon.awssdk.services.s3.model.GetObjectLockConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetObjectLockConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRetentionRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRetentionResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTorrentRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTorrentResponse;
import software.amazon.awssdk.services.s3.model.GetPublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.GetPublicAccessBlockResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketAnalyticsConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketAnalyticsConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketIntelligentTieringConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketIntelligentTieringConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketInventoryConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketInventoryConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketMetricsConfigurationsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketMetricsConfigurationsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketAccelerateConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAccelerateConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketAclRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAclResponse;
import software.amazon.awssdk.services.s3.model.PutBucketAnalyticsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketAnalyticsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionRequest;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.PutBucketIntelligentTieringConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketIntelligentTieringConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketInventoryConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketInventoryConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketLoggingRequest;
import software.amazon.awssdk.services.s3.model.PutBucketLoggingResponse;
import software.amazon.awssdk.services.s3.model.PutBucketMetricsConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketMetricsConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketOwnershipControlsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketOwnershipControlsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyResponse;
import software.amazon.awssdk.services.s3.model.PutBucketReplicationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketReplicationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketRequestPaymentRequest;
import software.amazon.awssdk.services.s3.model.PutBucketRequestPaymentResponse;
import software.amazon.awssdk.services.s3.model.PutBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningResponse;
import software.amazon.awssdk.services.s3.model.PutBucketWebsiteRequest;
import software.amazon.awssdk.services.s3.model.PutBucketWebsiteResponse;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclResponse;
import software.amazon.awssdk.services.s3.model.PutObjectLegalHoldRequest;
import software.amazon.awssdk.services.s3.model.PutObjectLegalHoldResponse;
import software.amazon.awssdk.services.s3.model.PutObjectLockConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutObjectLockConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRetentionRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRetentionResponse;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.PutPublicAccessBlockRequest;
import software.amazon.awssdk.services.s3.model.PutPublicAccessBlockResponse;
import software.amazon.awssdk.services.s3.model.RestoreObjectRequest;
import software.amazon.awssdk.services.s3.model.RestoreObjectResponse;
import software.amazon.awssdk.services.s3.model.SelectObjectContentRequest;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseRequest;
import software.amazon.awssdk.services.s3.model.WriteGetObjectResponseResponse;
import software.amazon.awssdk.services.s3.paginators.ListMultipartUploadsPublisher;
import software.amazon.awssdk.services.s3.paginators.ListObjectVersionsPublisher;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Publisher;
import software.amazon.awssdk.services.s3.paginators.ListPartsPublisher;
import software.amazon.awssdk.services.s3.waiters.S3AsyncWaiter;

@SdkInternalApi
public abstract class AbstractS3CrtAsyncClient implements S3CrtAsyncClient {
    abstract S3AsyncClient s3AsyncClient();

    @Override
    public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, AsyncRequestBody requestBody) {
        validateOverrideConfiguration(putObjectRequest);
        return s3AsyncClient().putObject(putObjectRequest, requestBody);
    }

    @Override
    public CompletableFuture<ListObjectsV2Response> listObjectsV2(ListObjectsV2Request listObjectsV2Request) {
        return s3AsyncClient().listObjectsV2(listObjectsV2Request);
    }

    @Override
    public CompletableFuture<CopyObjectResponse> copyObject(CopyObjectRequest copyObjectRequest) {
        validateOverrideConfiguration(copyObjectRequest);
        return s3AsyncClient().copyObject(copyObjectRequest);
    }

    @Override
    public CompletableFuture<HeadObjectResponse> headObject(HeadObjectRequest headObjectRequest) {
        validateOverrideConfiguration(headObjectRequest);
        return s3AsyncClient().headObject(headObjectRequest);
    }

    @Override
    public CompletableFuture<AbortMultipartUploadResponse> abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) {
        return s3AsyncClient().abortMultipartUpload(abortMultipartUploadRequest);
    }

    @Override
    public CompletableFuture<AbortMultipartUploadResponse> abortMultipartUpload(Consumer<AbortMultipartUploadRequest.Builder> abortMultipartUploadRequest) {
        return s3AsyncClient().abortMultipartUpload(abortMultipartUploadRequest);
    }

    @Override
    public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(CompleteMultipartUploadRequest completeMultipartUploadRequest) {
        return s3AsyncClient().completeMultipartUpload(completeMultipartUploadRequest);
    }

    @Override
    public CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(Consumer<CompleteMultipartUploadRequest.Builder> completeMultipartUploadRequest) {
        return s3AsyncClient().completeMultipartUpload(completeMultipartUploadRequest);
    }

    @Override
    public CompletableFuture<CopyObjectResponse> copyObject(Consumer<CopyObjectRequest.Builder> copyObjectRequest) {
        return s3AsyncClient().copyObject(copyObjectRequest);
    }

    @Override
    public CompletableFuture<CreateBucketResponse> createBucket(CreateBucketRequest createBucketRequest) {
        return s3AsyncClient().createBucket(createBucketRequest);
    }

    @Override
    public CompletableFuture<CreateBucketResponse> createBucket(Consumer<CreateBucketRequest.Builder> createBucketRequest) {
        return s3AsyncClient().createBucket(createBucketRequest);
    }

    @Override
    public CompletableFuture<CreateMultipartUploadResponse> createMultipartUpload(CreateMultipartUploadRequest createMultipartUploadRequest) {
        return s3AsyncClient().createMultipartUpload(createMultipartUploadRequest);
    }

    @Override
    public CompletableFuture<CreateMultipartUploadResponse> createMultipartUpload(Consumer<CreateMultipartUploadRequest.Builder> createMultipartUploadRequest) {
        return s3AsyncClient().createMultipartUpload(createMultipartUploadRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketResponse> deleteBucket(DeleteBucketRequest deleteBucketRequest) {
        return s3AsyncClient().deleteBucket(deleteBucketRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketResponse> deleteBucket(Consumer<DeleteBucketRequest.Builder> deleteBucketRequest) {
        return s3AsyncClient().deleteBucket(deleteBucketRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketAnalyticsConfigurationResponse> deleteBucketAnalyticsConfiguration(DeleteBucketAnalyticsConfigurationRequest deleteBucketAnalyticsConfigurationRequest) {
        return s3AsyncClient().deleteBucketAnalyticsConfiguration(deleteBucketAnalyticsConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketAnalyticsConfigurationResponse> deleteBucketAnalyticsConfiguration(Consumer<DeleteBucketAnalyticsConfigurationRequest.Builder> deleteBucketAnalyticsConfigurationRequest) {
        return s3AsyncClient().deleteBucketAnalyticsConfiguration(deleteBucketAnalyticsConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketCorsResponse> deleteBucketCors(DeleteBucketCorsRequest deleteBucketCorsRequest) {
        return s3AsyncClient().deleteBucketCors(deleteBucketCorsRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketCorsResponse> deleteBucketCors(Consumer<DeleteBucketCorsRequest.Builder> deleteBucketCorsRequest) {
        return s3AsyncClient().deleteBucketCors(deleteBucketCorsRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketEncryptionResponse> deleteBucketEncryption(DeleteBucketEncryptionRequest deleteBucketEncryptionRequest) {
        return s3AsyncClient().deleteBucketEncryption(deleteBucketEncryptionRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketEncryptionResponse> deleteBucketEncryption(Consumer<DeleteBucketEncryptionRequest.Builder> deleteBucketEncryptionRequest) {
        return s3AsyncClient().deleteBucketEncryption(deleteBucketEncryptionRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketIntelligentTieringConfigurationResponse> deleteBucketIntelligentTieringConfiguration(DeleteBucketIntelligentTieringConfigurationRequest deleteBucketIntelligentTieringConfigurationRequest) {
        return s3AsyncClient().deleteBucketIntelligentTieringConfiguration(deleteBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketIntelligentTieringConfigurationResponse> deleteBucketIntelligentTieringConfiguration(Consumer<DeleteBucketIntelligentTieringConfigurationRequest.Builder> deleteBucketIntelligentTieringConfigurationRequest) {
        return s3AsyncClient().deleteBucketIntelligentTieringConfiguration(deleteBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketInventoryConfigurationResponse> deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest deleteBucketInventoryConfigurationRequest) {
        return s3AsyncClient().deleteBucketInventoryConfiguration(deleteBucketInventoryConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketInventoryConfigurationResponse> deleteBucketInventoryConfiguration(Consumer<DeleteBucketInventoryConfigurationRequest.Builder> deleteBucketInventoryConfigurationRequest) {
        return s3AsyncClient().deleteBucketInventoryConfiguration(deleteBucketInventoryConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketLifecycleResponse> deleteBucketLifecycle(DeleteBucketLifecycleRequest deleteBucketLifecycleRequest) {
        return s3AsyncClient().deleteBucketLifecycle(deleteBucketLifecycleRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketLifecycleResponse> deleteBucketLifecycle(Consumer<DeleteBucketLifecycleRequest.Builder> deleteBucketLifecycleRequest) {
        return s3AsyncClient().deleteBucketLifecycle(deleteBucketLifecycleRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketMetricsConfigurationResponse> deleteBucketMetricsConfiguration(DeleteBucketMetricsConfigurationRequest deleteBucketMetricsConfigurationRequest) {
        return s3AsyncClient().deleteBucketMetricsConfiguration(deleteBucketMetricsConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketMetricsConfigurationResponse> deleteBucketMetricsConfiguration(Consumer<DeleteBucketMetricsConfigurationRequest.Builder> deleteBucketMetricsConfigurationRequest) {
        return s3AsyncClient().deleteBucketMetricsConfiguration(deleteBucketMetricsConfigurationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketOwnershipControlsResponse> deleteBucketOwnershipControls(DeleteBucketOwnershipControlsRequest deleteBucketOwnershipControlsRequest) {
        return s3AsyncClient().deleteBucketOwnershipControls(deleteBucketOwnershipControlsRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketOwnershipControlsResponse> deleteBucketOwnershipControls(Consumer<DeleteBucketOwnershipControlsRequest.Builder> deleteBucketOwnershipControlsRequest) {
        return s3AsyncClient().deleteBucketOwnershipControls(deleteBucketOwnershipControlsRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketPolicyResponse> deleteBucketPolicy(DeleteBucketPolicyRequest deleteBucketPolicyRequest) {
        return s3AsyncClient().deleteBucketPolicy(deleteBucketPolicyRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketPolicyResponse> deleteBucketPolicy(Consumer<DeleteBucketPolicyRequest.Builder> deleteBucketPolicyRequest) {
        return s3AsyncClient().deleteBucketPolicy(deleteBucketPolicyRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketReplicationResponse> deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest) {
        return s3AsyncClient().deleteBucketReplication(deleteBucketReplicationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketReplicationResponse> deleteBucketReplication(Consumer<DeleteBucketReplicationRequest.Builder> deleteBucketReplicationRequest) {
        return s3AsyncClient().deleteBucketReplication(deleteBucketReplicationRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketTaggingResponse> deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) {
        return s3AsyncClient().deleteBucketTagging(deleteBucketTaggingRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketTaggingResponse> deleteBucketTagging(Consumer<DeleteBucketTaggingRequest.Builder> deleteBucketTaggingRequest) {
        return s3AsyncClient().deleteBucketTagging(deleteBucketTaggingRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketWebsiteResponse> deleteBucketWebsite(DeleteBucketWebsiteRequest deleteBucketWebsiteRequest) {
        return s3AsyncClient().deleteBucketWebsite(deleteBucketWebsiteRequest);
    }

    @Override
    public CompletableFuture<DeleteBucketWebsiteResponse> deleteBucketWebsite(Consumer<DeleteBucketWebsiteRequest.Builder> deleteBucketWebsiteRequest) {
        return s3AsyncClient().deleteBucketWebsite(deleteBucketWebsiteRequest);
    }

    @Override
    public CompletableFuture<DeleteObjectResponse> deleteObject(DeleteObjectRequest deleteObjectRequest) {
        return s3AsyncClient().deleteObject(deleteObjectRequest);
    }

    @Override
    public CompletableFuture<DeleteObjectResponse> deleteObject(Consumer<DeleteObjectRequest.Builder> deleteObjectRequest) {
        return s3AsyncClient().deleteObject(deleteObjectRequest);
    }

    @Override
    public CompletableFuture<DeleteObjectTaggingResponse> deleteObjectTagging(DeleteObjectTaggingRequest deleteObjectTaggingRequest) {
        return s3AsyncClient().deleteObjectTagging(deleteObjectTaggingRequest);
    }

    @Override
    public CompletableFuture<DeleteObjectTaggingResponse> deleteObjectTagging(Consumer<DeleteObjectTaggingRequest.Builder> deleteObjectTaggingRequest) {
        return s3AsyncClient().deleteObjectTagging(deleteObjectTaggingRequest);
    }

    @Override
    public CompletableFuture<DeleteObjectsResponse> deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        return s3AsyncClient().deleteObjects(deleteObjectsRequest);
    }

    @Override
    public CompletableFuture<DeleteObjectsResponse> deleteObjects(Consumer<DeleteObjectsRequest.Builder> deleteObjectsRequest) {
        return s3AsyncClient().deleteObjects(deleteObjectsRequest);
    }

    @Override
    public CompletableFuture<DeletePublicAccessBlockResponse> deletePublicAccessBlock(DeletePublicAccessBlockRequest deletePublicAccessBlockRequest) {
        return s3AsyncClient().deletePublicAccessBlock(deletePublicAccessBlockRequest);
    }

    @Override
    public CompletableFuture<DeletePublicAccessBlockResponse> deletePublicAccessBlock(Consumer<DeletePublicAccessBlockRequest.Builder> deletePublicAccessBlockRequest) {
        return s3AsyncClient().deletePublicAccessBlock(deletePublicAccessBlockRequest);
    }

    @Override
    public CompletableFuture<GetBucketAccelerateConfigurationResponse> getBucketAccelerateConfiguration(GetBucketAccelerateConfigurationRequest getBucketAccelerateConfigurationRequest) {
        return s3AsyncClient().getBucketAccelerateConfiguration(getBucketAccelerateConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketAccelerateConfigurationResponse> getBucketAccelerateConfiguration(Consumer<GetBucketAccelerateConfigurationRequest.Builder> getBucketAccelerateConfigurationRequest) {
        return s3AsyncClient().getBucketAccelerateConfiguration(getBucketAccelerateConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketAclResponse> getBucketAcl(GetBucketAclRequest getBucketAclRequest) {
        return s3AsyncClient().getBucketAcl(getBucketAclRequest);
    }

    @Override
    public CompletableFuture<GetBucketAclResponse> getBucketAcl(Consumer<GetBucketAclRequest.Builder> getBucketAclRequest) {
        return s3AsyncClient().getBucketAcl(getBucketAclRequest);
    }

    @Override
    public CompletableFuture<GetBucketAnalyticsConfigurationResponse> getBucketAnalyticsConfiguration(GetBucketAnalyticsConfigurationRequest getBucketAnalyticsConfigurationRequest) {
        return s3AsyncClient().getBucketAnalyticsConfiguration(getBucketAnalyticsConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketAnalyticsConfigurationResponse> getBucketAnalyticsConfiguration(Consumer<GetBucketAnalyticsConfigurationRequest.Builder> getBucketAnalyticsConfigurationRequest) {
        return s3AsyncClient().getBucketAnalyticsConfiguration(getBucketAnalyticsConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketCorsResponse> getBucketCors(GetBucketCorsRequest getBucketCorsRequest) {
        return s3AsyncClient().getBucketCors(getBucketCorsRequest);
    }

    @Override
    public CompletableFuture<GetBucketCorsResponse> getBucketCors(Consumer<GetBucketCorsRequest.Builder> getBucketCorsRequest) {
        return s3AsyncClient().getBucketCors(getBucketCorsRequest);
    }

    @Override
    public CompletableFuture<GetBucketEncryptionResponse> getBucketEncryption(GetBucketEncryptionRequest getBucketEncryptionRequest) {
        return s3AsyncClient().getBucketEncryption(getBucketEncryptionRequest);
    }

    @Override
    public CompletableFuture<GetBucketEncryptionResponse> getBucketEncryption(Consumer<GetBucketEncryptionRequest.Builder> getBucketEncryptionRequest) {
        return s3AsyncClient().getBucketEncryption(getBucketEncryptionRequest);
    }

    @Override
    public CompletableFuture<GetBucketIntelligentTieringConfigurationResponse> getBucketIntelligentTieringConfiguration(GetBucketIntelligentTieringConfigurationRequest getBucketIntelligentTieringConfigurationRequest) {
        return s3AsyncClient().getBucketIntelligentTieringConfiguration(getBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketIntelligentTieringConfigurationResponse> getBucketIntelligentTieringConfiguration(Consumer<GetBucketIntelligentTieringConfigurationRequest.Builder> getBucketIntelligentTieringConfigurationRequest) {
        return s3AsyncClient().getBucketIntelligentTieringConfiguration(getBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketInventoryConfigurationResponse> getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest getBucketInventoryConfigurationRequest) {
        return s3AsyncClient().getBucketInventoryConfiguration(getBucketInventoryConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketInventoryConfigurationResponse> getBucketInventoryConfiguration(Consumer<GetBucketInventoryConfigurationRequest.Builder> getBucketInventoryConfigurationRequest) {
        return s3AsyncClient().getBucketInventoryConfiguration(getBucketInventoryConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketLifecycleConfigurationResponse> getBucketLifecycleConfiguration(GetBucketLifecycleConfigurationRequest getBucketLifecycleConfigurationRequest) {
        return s3AsyncClient().getBucketLifecycleConfiguration(getBucketLifecycleConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketLifecycleConfigurationResponse> getBucketLifecycleConfiguration(Consumer<GetBucketLifecycleConfigurationRequest.Builder> getBucketLifecycleConfigurationRequest) {
        return s3AsyncClient().getBucketLifecycleConfiguration(getBucketLifecycleConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketLocationResponse> getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) {
        return s3AsyncClient().getBucketLocation(getBucketLocationRequest);
    }

    @Override
    public CompletableFuture<GetBucketLocationResponse> getBucketLocation(Consumer<GetBucketLocationRequest.Builder> getBucketLocationRequest) {
        return s3AsyncClient().getBucketLocation(getBucketLocationRequest);
    }

    @Override
    public CompletableFuture<GetBucketLoggingResponse> getBucketLogging(GetBucketLoggingRequest getBucketLoggingRequest) {
        return s3AsyncClient().getBucketLogging(getBucketLoggingRequest);
    }

    @Override
    public CompletableFuture<GetBucketLoggingResponse> getBucketLogging(Consumer<GetBucketLoggingRequest.Builder> getBucketLoggingRequest) {
        return s3AsyncClient().getBucketLogging(getBucketLoggingRequest);
    }

    @Override
    public CompletableFuture<GetBucketMetricsConfigurationResponse> getBucketMetricsConfiguration(GetBucketMetricsConfigurationRequest getBucketMetricsConfigurationRequest) {
        return s3AsyncClient().getBucketMetricsConfiguration(getBucketMetricsConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketMetricsConfigurationResponse> getBucketMetricsConfiguration(Consumer<GetBucketMetricsConfigurationRequest.Builder> getBucketMetricsConfigurationRequest) {
        return s3AsyncClient().getBucketMetricsConfiguration(getBucketMetricsConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketNotificationConfigurationResponse> getBucketNotificationConfiguration(GetBucketNotificationConfigurationRequest getBucketNotificationConfigurationRequest) {
        return s3AsyncClient().getBucketNotificationConfiguration(getBucketNotificationConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketNotificationConfigurationResponse> getBucketNotificationConfiguration(Consumer<GetBucketNotificationConfigurationRequest.Builder> getBucketNotificationConfigurationRequest) {
        return s3AsyncClient().getBucketNotificationConfiguration(getBucketNotificationConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetBucketOwnershipControlsResponse> getBucketOwnershipControls(GetBucketOwnershipControlsRequest getBucketOwnershipControlsRequest) {
        return s3AsyncClient().getBucketOwnershipControls(getBucketOwnershipControlsRequest);
    }

    @Override
    public CompletableFuture<GetBucketOwnershipControlsResponse> getBucketOwnershipControls(Consumer<GetBucketOwnershipControlsRequest.Builder> getBucketOwnershipControlsRequest) {
        return s3AsyncClient().getBucketOwnershipControls(getBucketOwnershipControlsRequest);
    }

    @Override
    public CompletableFuture<GetBucketPolicyResponse> getBucketPolicy(GetBucketPolicyRequest getBucketPolicyRequest) {
        return s3AsyncClient().getBucketPolicy(getBucketPolicyRequest);
    }

    @Override
    public CompletableFuture<GetBucketPolicyResponse> getBucketPolicy(Consumer<GetBucketPolicyRequest.Builder> getBucketPolicyRequest) {
        return s3AsyncClient().getBucketPolicy(getBucketPolicyRequest);
    }

    @Override
    public CompletableFuture<GetBucketPolicyStatusResponse> getBucketPolicyStatus(GetBucketPolicyStatusRequest getBucketPolicyStatusRequest) {
        return s3AsyncClient().getBucketPolicyStatus(getBucketPolicyStatusRequest);
    }

    @Override
    public CompletableFuture<GetBucketPolicyStatusResponse> getBucketPolicyStatus(Consumer<GetBucketPolicyStatusRequest.Builder> getBucketPolicyStatusRequest) {
        return s3AsyncClient().getBucketPolicyStatus(getBucketPolicyStatusRequest);
    }

    @Override
    public CompletableFuture<GetBucketReplicationResponse> getBucketReplication(GetBucketReplicationRequest getBucketReplicationRequest) {
        return s3AsyncClient().getBucketReplication(getBucketReplicationRequest);
    }

    @Override
    public CompletableFuture<GetBucketReplicationResponse> getBucketReplication(Consumer<GetBucketReplicationRequest.Builder> getBucketReplicationRequest) {
        return s3AsyncClient().getBucketReplication(getBucketReplicationRequest);
    }

    @Override
    public CompletableFuture<GetBucketRequestPaymentResponse> getBucketRequestPayment(GetBucketRequestPaymentRequest getBucketRequestPaymentRequest) {
        return s3AsyncClient().getBucketRequestPayment(getBucketRequestPaymentRequest);
    }

    @Override
    public CompletableFuture<GetBucketRequestPaymentResponse> getBucketRequestPayment(Consumer<GetBucketRequestPaymentRequest.Builder> getBucketRequestPaymentRequest) {
        return s3AsyncClient().getBucketRequestPayment(getBucketRequestPaymentRequest);
    }

    @Override
    public CompletableFuture<GetBucketTaggingResponse> getBucketTagging(GetBucketTaggingRequest getBucketTaggingRequest) {
        return s3AsyncClient().getBucketTagging(getBucketTaggingRequest);
    }

    @Override
    public CompletableFuture<GetBucketTaggingResponse> getBucketTagging(Consumer<GetBucketTaggingRequest.Builder> getBucketTaggingRequest) {
        return s3AsyncClient().getBucketTagging(getBucketTaggingRequest);
    }

    @Override
    public CompletableFuture<GetBucketVersioningResponse> getBucketVersioning(GetBucketVersioningRequest getBucketVersioningRequest) {
        return s3AsyncClient().getBucketVersioning(getBucketVersioningRequest);
    }

    @Override
    public CompletableFuture<GetBucketVersioningResponse> getBucketVersioning(Consumer<GetBucketVersioningRequest.Builder> getBucketVersioningRequest) {
        return s3AsyncClient().getBucketVersioning(getBucketVersioningRequest);
    }

    @Override
    public CompletableFuture<GetBucketWebsiteResponse> getBucketWebsite(GetBucketWebsiteRequest getBucketWebsiteRequest) {
        return s3AsyncClient().getBucketWebsite(getBucketWebsiteRequest);
    }

    @Override
    public CompletableFuture<GetBucketWebsiteResponse> getBucketWebsite(Consumer<GetBucketWebsiteRequest.Builder> getBucketWebsiteRequest) {
        return s3AsyncClient().getBucketWebsite(getBucketWebsiteRequest);
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObject(Consumer<GetObjectRequest.Builder> getObjectRequest, AsyncResponseTransformer<GetObjectResponse, ReturnT> asyncResponseTransformer) {
        return s3AsyncClient().getObject(getObjectRequest, asyncResponseTransformer);
    }

    @Override
    public CompletableFuture<GetObjectResponse> getObject(GetObjectRequest getObjectRequest, Path destinationPath) {
        return s3AsyncClient().getObject(getObjectRequest, destinationPath);
    }

    @Override
    public CompletableFuture<GetObjectResponse> getObject(Consumer<GetObjectRequest.Builder> getObjectRequest,
                                                          Path destinationPath) {
        return s3AsyncClient().getObject(getObjectRequest, destinationPath);
    }

    @Override
    public CompletableFuture<GetObjectAclResponse> getObjectAcl(GetObjectAclRequest getObjectAclRequest) {
        return s3AsyncClient().getObjectAcl(getObjectAclRequest);
    }

    @Override
    public CompletableFuture<GetObjectAclResponse> getObjectAcl(Consumer<GetObjectAclRequest.Builder> getObjectAclRequest) {
        return s3AsyncClient().getObjectAcl(getObjectAclRequest);
    }

    @Override
    public CompletableFuture<GetObjectAttributesResponse> getObjectAttributes(GetObjectAttributesRequest getObjectAttributesRequest) {
        return s3AsyncClient().getObjectAttributes(getObjectAttributesRequest);
    }

    @Override
    public CompletableFuture<GetObjectAttributesResponse> getObjectAttributes(Consumer<GetObjectAttributesRequest.Builder> getObjectAttributesRequest) {
        return s3AsyncClient().getObjectAttributes(getObjectAttributesRequest);
    }

    @Override
    public CompletableFuture<GetObjectLegalHoldResponse> getObjectLegalHold(GetObjectLegalHoldRequest getObjectLegalHoldRequest) {
        return s3AsyncClient().getObjectLegalHold(getObjectLegalHoldRequest);
    }

    @Override
    public CompletableFuture<GetObjectLegalHoldResponse> getObjectLegalHold(Consumer<GetObjectLegalHoldRequest.Builder> getObjectLegalHoldRequest) {
        return s3AsyncClient().getObjectLegalHold(getObjectLegalHoldRequest);
    }

    @Override
    public CompletableFuture<GetObjectLockConfigurationResponse> getObjectLockConfiguration(GetObjectLockConfigurationRequest getObjectLockConfigurationRequest) {
        return s3AsyncClient().getObjectLockConfiguration(getObjectLockConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetObjectLockConfigurationResponse> getObjectLockConfiguration(Consumer<GetObjectLockConfigurationRequest.Builder> getObjectLockConfigurationRequest) {
        return s3AsyncClient().getObjectLockConfiguration(getObjectLockConfigurationRequest);
    }

    @Override
    public CompletableFuture<GetObjectRetentionResponse> getObjectRetention(GetObjectRetentionRequest getObjectRetentionRequest) {
        return s3AsyncClient().getObjectRetention(getObjectRetentionRequest);
    }

    @Override
    public CompletableFuture<GetObjectRetentionResponse> getObjectRetention(Consumer<GetObjectRetentionRequest.Builder> getObjectRetentionRequest) {
        return s3AsyncClient().getObjectRetention(getObjectRetentionRequest);
    }

    @Override
    public CompletableFuture<GetObjectTaggingResponse> getObjectTagging(GetObjectTaggingRequest getObjectTaggingRequest) {
        return s3AsyncClient().getObjectTagging(getObjectTaggingRequest);
    }

    @Override
    public CompletableFuture<GetObjectTaggingResponse> getObjectTagging(Consumer<GetObjectTaggingRequest.Builder> getObjectTaggingRequest) {
        return s3AsyncClient().getObjectTagging(getObjectTaggingRequest);
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest,
                                                                 AsyncResponseTransformer<GetObjectTorrentResponse, ReturnT> asyncResponseTransformer) {
        return s3AsyncClient().getObjectTorrent(getObjectTorrentRequest, asyncResponseTransformer);
    }

    @Override
    public <ReturnT> CompletableFuture<ReturnT> getObjectTorrent(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest, AsyncResponseTransformer<GetObjectTorrentResponse, ReturnT> asyncResponseTransformer) {
        return s3AsyncClient().getObjectTorrent(getObjectTorrentRequest, asyncResponseTransformer);
    }

    @Override
    public CompletableFuture<GetObjectTorrentResponse> getObjectTorrent(GetObjectTorrentRequest getObjectTorrentRequest, Path destinationPath) {
        return s3AsyncClient().getObjectTorrent(getObjectTorrentRequest, destinationPath);
    }

    @Override
    public CompletableFuture<GetObjectTorrentResponse> getObjectTorrent(Consumer<GetObjectTorrentRequest.Builder> getObjectTorrentRequest, Path destinationPath) {
        return s3AsyncClient().getObjectTorrent(getObjectTorrentRequest, destinationPath);
    }

    @Override
    public CompletableFuture<GetPublicAccessBlockResponse> getPublicAccessBlock(GetPublicAccessBlockRequest getPublicAccessBlockRequest) {
        return s3AsyncClient().getPublicAccessBlock(getPublicAccessBlockRequest);
    }

    @Override
    public CompletableFuture<GetPublicAccessBlockResponse> getPublicAccessBlock(Consumer<GetPublicAccessBlockRequest.Builder> getPublicAccessBlockRequest) {
        return s3AsyncClient().getPublicAccessBlock(getPublicAccessBlockRequest);
    }

    @Override
    public CompletableFuture<HeadBucketResponse> headBucket(HeadBucketRequest headBucketRequest) {
        return s3AsyncClient().headBucket(headBucketRequest);
    }

    @Override
    public CompletableFuture<HeadBucketResponse> headBucket(Consumer<HeadBucketRequest.Builder> headBucketRequest) {
        return s3AsyncClient().headBucket(headBucketRequest);
    }

    @Override
    public CompletableFuture<HeadObjectResponse> headObject(Consumer<HeadObjectRequest.Builder> headObjectRequest) {
        return s3AsyncClient().headObject(headObjectRequest);
    }

    @Override
    public CompletableFuture<ListBucketAnalyticsConfigurationsResponse> listBucketAnalyticsConfigurations(ListBucketAnalyticsConfigurationsRequest listBucketAnalyticsConfigurationsRequest) {
        return s3AsyncClient().listBucketAnalyticsConfigurations(listBucketAnalyticsConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketAnalyticsConfigurationsResponse> listBucketAnalyticsConfigurations(Consumer<ListBucketAnalyticsConfigurationsRequest.Builder> listBucketAnalyticsConfigurationsRequest) {
        return s3AsyncClient().listBucketAnalyticsConfigurations(listBucketAnalyticsConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketIntelligentTieringConfigurationsResponse> listBucketIntelligentTieringConfigurations(ListBucketIntelligentTieringConfigurationsRequest listBucketIntelligentTieringConfigurationsRequest) {
        return s3AsyncClient().listBucketIntelligentTieringConfigurations(listBucketIntelligentTieringConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketIntelligentTieringConfigurationsResponse> listBucketIntelligentTieringConfigurations(Consumer<ListBucketIntelligentTieringConfigurationsRequest.Builder> listBucketIntelligentTieringConfigurationsRequest) {
        return s3AsyncClient().listBucketIntelligentTieringConfigurations(listBucketIntelligentTieringConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketInventoryConfigurationsResponse> listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest listBucketInventoryConfigurationsRequest) {
        return s3AsyncClient().listBucketInventoryConfigurations(listBucketInventoryConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketInventoryConfigurationsResponse> listBucketInventoryConfigurations(Consumer<ListBucketInventoryConfigurationsRequest.Builder> listBucketInventoryConfigurationsRequest) {
        return s3AsyncClient().listBucketInventoryConfigurations(listBucketInventoryConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketMetricsConfigurationsResponse> listBucketMetricsConfigurations(ListBucketMetricsConfigurationsRequest listBucketMetricsConfigurationsRequest) {
        return s3AsyncClient().listBucketMetricsConfigurations(listBucketMetricsConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketMetricsConfigurationsResponse> listBucketMetricsConfigurations(Consumer<ListBucketMetricsConfigurationsRequest.Builder> listBucketMetricsConfigurationsRequest) {
        return s3AsyncClient().listBucketMetricsConfigurations(listBucketMetricsConfigurationsRequest);
    }

    @Override
    public CompletableFuture<ListBucketsResponse> listBuckets(ListBucketsRequest listBucketsRequest) {
        return s3AsyncClient().listBuckets(listBucketsRequest);
    }

    @Override
    public CompletableFuture<ListBucketsResponse> listBuckets(Consumer<ListBucketsRequest.Builder> listBucketsRequest) {
        return s3AsyncClient().listBuckets(listBucketsRequest);
    }

    @Override
    public CompletableFuture<ListBucketsResponse> listBuckets() {
        return s3AsyncClient().listBuckets();
    }

    @Override
    public CompletableFuture<ListMultipartUploadsResponse> listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest) {
        return s3AsyncClient().listMultipartUploads(listMultipartUploadsRequest);
    }

    @Override
    public CompletableFuture<ListMultipartUploadsResponse> listMultipartUploads(Consumer<ListMultipartUploadsRequest.Builder> listMultipartUploadsRequest) {
        return s3AsyncClient().listMultipartUploads(listMultipartUploadsRequest);
    }

    @Override
    public ListMultipartUploadsPublisher listMultipartUploadsPaginator(ListMultipartUploadsRequest listMultipartUploadsRequest) {
        return s3AsyncClient().listMultipartUploadsPaginator(listMultipartUploadsRequest);
    }

    @Override
    public ListMultipartUploadsPublisher listMultipartUploadsPaginator(Consumer<ListMultipartUploadsRequest.Builder> listMultipartUploadsRequest) {
        return s3AsyncClient().listMultipartUploadsPaginator(listMultipartUploadsRequest);
    }

    @Override
    public CompletableFuture<ListObjectVersionsResponse> listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) {
        return s3AsyncClient().listObjectVersions(listObjectVersionsRequest);
    }

    @Override
    public CompletableFuture<ListObjectVersionsResponse> listObjectVersions(Consumer<ListObjectVersionsRequest.Builder> listObjectVersionsRequest) {
        return s3AsyncClient().listObjectVersions(listObjectVersionsRequest);
    }

    @Override
    public ListObjectVersionsPublisher listObjectVersionsPaginator(ListObjectVersionsRequest listObjectVersionsRequest) {
        return s3AsyncClient().listObjectVersionsPaginator(listObjectVersionsRequest);
    }

    @Override
    public ListObjectVersionsPublisher listObjectVersionsPaginator(Consumer<ListObjectVersionsRequest.Builder> listObjectVersionsRequest) {
        return s3AsyncClient().listObjectVersionsPaginator(listObjectVersionsRequest);
    }

    @Override
    public CompletableFuture<ListObjectsResponse> listObjects(ListObjectsRequest listObjectsRequest) {
        return s3AsyncClient().listObjects(listObjectsRequest);
    }

    @Override
    public CompletableFuture<ListObjectsResponse> listObjects(Consumer<ListObjectsRequest.Builder> listObjectsRequest) {
        return s3AsyncClient().listObjects(listObjectsRequest);
    }

    @Override
    public CompletableFuture<ListObjectsV2Response> listObjectsV2(Consumer<ListObjectsV2Request.Builder> listObjectsV2Request) {
        return s3AsyncClient().listObjectsV2(listObjectsV2Request);
    }

    @Override
    public ListObjectsV2Publisher listObjectsV2Paginator(ListObjectsV2Request listObjectsV2Request) {
        return s3AsyncClient().listObjectsV2Paginator(listObjectsV2Request);
    }

    @Override
    public ListObjectsV2Publisher listObjectsV2Paginator(Consumer<ListObjectsV2Request.Builder> listObjectsV2Request) {
        return s3AsyncClient().listObjectsV2Paginator(listObjectsV2Request);
    }

    @Override
    public CompletableFuture<ListPartsResponse> listParts(ListPartsRequest listPartsRequest) {
        return s3AsyncClient().listParts(listPartsRequest);
    }

    @Override
    public CompletableFuture<ListPartsResponse> listParts(Consumer<ListPartsRequest.Builder> listPartsRequest) {
        return s3AsyncClient().listParts(listPartsRequest);
    }

    @Override
    public ListPartsPublisher listPartsPaginator(ListPartsRequest listPartsRequest) {
        return s3AsyncClient().listPartsPaginator(listPartsRequest);
    }

    @Override
    public ListPartsPublisher listPartsPaginator(Consumer<ListPartsRequest.Builder> listPartsRequest) {
        return s3AsyncClient().listPartsPaginator(listPartsRequest);
    }

    @Override
    public CompletableFuture<PutBucketAccelerateConfigurationResponse> putBucketAccelerateConfiguration(PutBucketAccelerateConfigurationRequest putBucketAccelerateConfigurationRequest) {
        return s3AsyncClient().putBucketAccelerateConfiguration(putBucketAccelerateConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketAccelerateConfigurationResponse> putBucketAccelerateConfiguration(Consumer<PutBucketAccelerateConfigurationRequest.Builder> putBucketAccelerateConfigurationRequest) {
        return s3AsyncClient().putBucketAccelerateConfiguration(putBucketAccelerateConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketAclResponse> putBucketAcl(PutBucketAclRequest putBucketAclRequest) {
        return s3AsyncClient().putBucketAcl(putBucketAclRequest);
    }

    @Override
    public CompletableFuture<PutBucketAclResponse> putBucketAcl(Consumer<PutBucketAclRequest.Builder> putBucketAclRequest) {
        return s3AsyncClient().putBucketAcl(putBucketAclRequest);
    }

    @Override
    public CompletableFuture<PutBucketAnalyticsConfigurationResponse> putBucketAnalyticsConfiguration(PutBucketAnalyticsConfigurationRequest putBucketAnalyticsConfigurationRequest) {
        return s3AsyncClient().putBucketAnalyticsConfiguration(putBucketAnalyticsConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketAnalyticsConfigurationResponse> putBucketAnalyticsConfiguration(Consumer<PutBucketAnalyticsConfigurationRequest.Builder> putBucketAnalyticsConfigurationRequest) {
        return s3AsyncClient().putBucketAnalyticsConfiguration(putBucketAnalyticsConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketCorsResponse> putBucketCors(PutBucketCorsRequest putBucketCorsRequest) {
        return s3AsyncClient().putBucketCors(putBucketCorsRequest);
    }

    @Override
    public CompletableFuture<PutBucketCorsResponse> putBucketCors(Consumer<PutBucketCorsRequest.Builder> putBucketCorsRequest) {
        return s3AsyncClient().putBucketCors(putBucketCorsRequest);
    }

    @Override
    public CompletableFuture<PutBucketEncryptionResponse> putBucketEncryption(PutBucketEncryptionRequest putBucketEncryptionRequest) {
        return s3AsyncClient().putBucketEncryption(putBucketEncryptionRequest);
    }

    @Override
    public CompletableFuture<PutBucketEncryptionResponse> putBucketEncryption(Consumer<PutBucketEncryptionRequest.Builder> putBucketEncryptionRequest) {
        return s3AsyncClient().putBucketEncryption(putBucketEncryptionRequest);
    }

    @Override
    public CompletableFuture<PutBucketIntelligentTieringConfigurationResponse> putBucketIntelligentTieringConfiguration(PutBucketIntelligentTieringConfigurationRequest putBucketIntelligentTieringConfigurationRequest) {
        return s3AsyncClient().putBucketIntelligentTieringConfiguration(putBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketIntelligentTieringConfigurationResponse> putBucketIntelligentTieringConfiguration(Consumer<PutBucketIntelligentTieringConfigurationRequest.Builder> putBucketIntelligentTieringConfigurationRequest) {
        return s3AsyncClient().putBucketIntelligentTieringConfiguration(putBucketIntelligentTieringConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketInventoryConfigurationResponse> putBucketInventoryConfiguration(PutBucketInventoryConfigurationRequest putBucketInventoryConfigurationRequest) {
        return s3AsyncClient().putBucketInventoryConfiguration(putBucketInventoryConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketInventoryConfigurationResponse> putBucketInventoryConfiguration(Consumer<PutBucketInventoryConfigurationRequest.Builder> putBucketInventoryConfigurationRequest) {
        return s3AsyncClient().putBucketInventoryConfiguration(putBucketInventoryConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketLifecycleConfigurationResponse> putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest) {
        return s3AsyncClient().putBucketLifecycleConfiguration(putBucketLifecycleConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketLifecycleConfigurationResponse> putBucketLifecycleConfiguration(Consumer<PutBucketLifecycleConfigurationRequest.Builder> putBucketLifecycleConfigurationRequest) {
        return s3AsyncClient().putBucketLifecycleConfiguration(putBucketLifecycleConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketLoggingResponse> putBucketLogging(PutBucketLoggingRequest putBucketLoggingRequest) {
        return s3AsyncClient().putBucketLogging(putBucketLoggingRequest);
    }

    @Override
    public CompletableFuture<PutBucketLoggingResponse> putBucketLogging(Consumer<PutBucketLoggingRequest.Builder> putBucketLoggingRequest) {
        return s3AsyncClient().putBucketLogging(putBucketLoggingRequest);
    }

    @Override
    public CompletableFuture<PutBucketMetricsConfigurationResponse> putBucketMetricsConfiguration(PutBucketMetricsConfigurationRequest putBucketMetricsConfigurationRequest) {
        return s3AsyncClient().putBucketMetricsConfiguration(putBucketMetricsConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketMetricsConfigurationResponse> putBucketMetricsConfiguration(Consumer<PutBucketMetricsConfigurationRequest.Builder> putBucketMetricsConfigurationRequest) {
        return s3AsyncClient().putBucketMetricsConfiguration(putBucketMetricsConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketNotificationConfigurationResponse> putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest putBucketNotificationConfigurationRequest) {
        return s3AsyncClient().putBucketNotificationConfiguration(putBucketNotificationConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketNotificationConfigurationResponse> putBucketNotificationConfiguration(Consumer<PutBucketNotificationConfigurationRequest.Builder> putBucketNotificationConfigurationRequest) {
        return s3AsyncClient().putBucketNotificationConfiguration(putBucketNotificationConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutBucketOwnershipControlsResponse> putBucketOwnershipControls(PutBucketOwnershipControlsRequest putBucketOwnershipControlsRequest) {
        return s3AsyncClient().putBucketOwnershipControls(putBucketOwnershipControlsRequest);
    }

    @Override
    public CompletableFuture<PutBucketOwnershipControlsResponse> putBucketOwnershipControls(Consumer<PutBucketOwnershipControlsRequest.Builder> putBucketOwnershipControlsRequest) {
        return s3AsyncClient().putBucketOwnershipControls(putBucketOwnershipControlsRequest);
    }

    @Override
    public CompletableFuture<PutBucketPolicyResponse> putBucketPolicy(PutBucketPolicyRequest putBucketPolicyRequest) {
        return s3AsyncClient().putBucketPolicy(putBucketPolicyRequest);
    }

    @Override
    public CompletableFuture<PutBucketPolicyResponse> putBucketPolicy(Consumer<PutBucketPolicyRequest.Builder> putBucketPolicyRequest) {
        return s3AsyncClient().putBucketPolicy(putBucketPolicyRequest);
    }

    @Override
    public CompletableFuture<PutBucketReplicationResponse> putBucketReplication(PutBucketReplicationRequest putBucketReplicationRequest) {
        return s3AsyncClient().putBucketReplication(putBucketReplicationRequest);
    }

    @Override
    public CompletableFuture<PutBucketReplicationResponse> putBucketReplication(Consumer<PutBucketReplicationRequest.Builder> putBucketReplicationRequest) {
        return s3AsyncClient().putBucketReplication(putBucketReplicationRequest);
    }

    @Override
    public CompletableFuture<PutBucketRequestPaymentResponse> putBucketRequestPayment(PutBucketRequestPaymentRequest putBucketRequestPaymentRequest) {
        return s3AsyncClient().putBucketRequestPayment(putBucketRequestPaymentRequest);
    }

    @Override
    public CompletableFuture<PutBucketRequestPaymentResponse> putBucketRequestPayment(Consumer<PutBucketRequestPaymentRequest.Builder> putBucketRequestPaymentRequest) {
        return s3AsyncClient().putBucketRequestPayment(putBucketRequestPaymentRequest);
    }

    @Override
    public CompletableFuture<PutBucketTaggingResponse> putBucketTagging(PutBucketTaggingRequest putBucketTaggingRequest) {
        return s3AsyncClient().putBucketTagging(putBucketTaggingRequest);
    }

    @Override
    public CompletableFuture<PutBucketTaggingResponse> putBucketTagging(Consumer<PutBucketTaggingRequest.Builder> putBucketTaggingRequest) {
        return s3AsyncClient().putBucketTagging(putBucketTaggingRequest);
    }

    @Override
    public CompletableFuture<PutBucketVersioningResponse> putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) {
        return s3AsyncClient().putBucketVersioning(putBucketVersioningRequest);
    }

    @Override
    public CompletableFuture<PutBucketVersioningResponse> putBucketVersioning(Consumer<PutBucketVersioningRequest.Builder> putBucketVersioningRequest) {
        return s3AsyncClient().putBucketVersioning(putBucketVersioningRequest);
    }

    @Override
    public CompletableFuture<PutBucketWebsiteResponse> putBucketWebsite(PutBucketWebsiteRequest putBucketWebsiteRequest) {
        return s3AsyncClient().putBucketWebsite(putBucketWebsiteRequest);
    }

    @Override
    public CompletableFuture<PutBucketWebsiteResponse> putBucketWebsite(Consumer<PutBucketWebsiteRequest.Builder> putBucketWebsiteRequest) {
        return s3AsyncClient().putBucketWebsite(putBucketWebsiteRequest);
    }

    @Override
    public CompletableFuture<PutObjectResponse> putObject(Consumer<PutObjectRequest.Builder> putObjectRequest,
                                                          AsyncRequestBody requestBody) {
        return s3AsyncClient().putObject(putObjectRequest, requestBody);
    }

    @Override
    public CompletableFuture<PutObjectResponse> putObject(PutObjectRequest putObjectRequest, Path sourcePath) {
        return s3AsyncClient().putObject(putObjectRequest, sourcePath);
    }

    @Override
    public CompletableFuture<PutObjectResponse> putObject(Consumer<PutObjectRequest.Builder> putObjectRequest, Path sourcePath) {
        return s3AsyncClient().putObject(putObjectRequest, sourcePath);
    }

    @Override
    public CompletableFuture<PutObjectAclResponse> putObjectAcl(PutObjectAclRequest putObjectAclRequest) {
        return s3AsyncClient().putObjectAcl(putObjectAclRequest);
    }

    @Override
    public CompletableFuture<PutObjectAclResponse> putObjectAcl(Consumer<PutObjectAclRequest.Builder> putObjectAclRequest) {
        return s3AsyncClient().putObjectAcl(putObjectAclRequest);
    }

    @Override
    public CompletableFuture<PutObjectLegalHoldResponse> putObjectLegalHold(PutObjectLegalHoldRequest putObjectLegalHoldRequest) {
        return s3AsyncClient().putObjectLegalHold(putObjectLegalHoldRequest);
    }

    @Override
    public CompletableFuture<PutObjectLegalHoldResponse> putObjectLegalHold(Consumer<PutObjectLegalHoldRequest.Builder> putObjectLegalHoldRequest) {
        return s3AsyncClient().putObjectLegalHold(putObjectLegalHoldRequest);
    }

    @Override
    public CompletableFuture<PutObjectLockConfigurationResponse> putObjectLockConfiguration(PutObjectLockConfigurationRequest putObjectLockConfigurationRequest) {
        return s3AsyncClient().putObjectLockConfiguration(putObjectLockConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutObjectLockConfigurationResponse> putObjectLockConfiguration(Consumer<PutObjectLockConfigurationRequest.Builder> putObjectLockConfigurationRequest) {
        return s3AsyncClient().putObjectLockConfiguration(putObjectLockConfigurationRequest);
    }

    @Override
    public CompletableFuture<PutObjectRetentionResponse> putObjectRetention(PutObjectRetentionRequest putObjectRetentionRequest) {
        return s3AsyncClient().putObjectRetention(putObjectRetentionRequest);
    }

    @Override
    public CompletableFuture<PutObjectRetentionResponse> putObjectRetention(Consumer<PutObjectRetentionRequest.Builder> putObjectRetentionRequest) {
        return s3AsyncClient().putObjectRetention(putObjectRetentionRequest);
    }

    @Override
    public CompletableFuture<PutObjectTaggingResponse> putObjectTagging(PutObjectTaggingRequest putObjectTaggingRequest) {
        return s3AsyncClient().putObjectTagging(putObjectTaggingRequest);
    }

    @Override
    public CompletableFuture<PutObjectTaggingResponse> putObjectTagging(Consumer<PutObjectTaggingRequest.Builder> putObjectTaggingRequest) {
        return s3AsyncClient().putObjectTagging(putObjectTaggingRequest);
    }

    @Override
    public CompletableFuture<PutPublicAccessBlockResponse> putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) {
        return s3AsyncClient().putPublicAccessBlock(putPublicAccessBlockRequest);
    }

    @Override
    public CompletableFuture<PutPublicAccessBlockResponse> putPublicAccessBlock(Consumer<PutPublicAccessBlockRequest.Builder> putPublicAccessBlockRequest) {
        return s3AsyncClient().putPublicAccessBlock(putPublicAccessBlockRequest);
    }

    @Override
    public CompletableFuture<RestoreObjectResponse> restoreObject(RestoreObjectRequest restoreObjectRequest) {
        return s3AsyncClient().restoreObject(restoreObjectRequest);
    }

    @Override
    public CompletableFuture<RestoreObjectResponse> restoreObject(Consumer<RestoreObjectRequest.Builder> restoreObjectRequest) {
        return s3AsyncClient().restoreObject(restoreObjectRequest);
    }

    @Override
    public CompletableFuture<Void> selectObjectContent(SelectObjectContentRequest selectObjectContentRequest, SelectObjectContentResponseHandler asyncResponseHandler) {
        return s3AsyncClient().selectObjectContent(selectObjectContentRequest, asyncResponseHandler);
    }

    @Override
    public CompletableFuture<Void> selectObjectContent(Consumer<SelectObjectContentRequest.Builder> selectObjectContentRequest,
                                                       SelectObjectContentResponseHandler asyncResponseHandler) {
        return s3AsyncClient().selectObjectContent(selectObjectContentRequest, asyncResponseHandler);
    }

    @Override
    public CompletableFuture<UploadPartResponse> uploadPart(UploadPartRequest uploadPartRequest, AsyncRequestBody requestBody) {
        return s3AsyncClient().uploadPart(uploadPartRequest, requestBody);
    }

    @Override
    public CompletableFuture<UploadPartResponse> uploadPart(Consumer<UploadPartRequest.Builder> uploadPartRequest,
                                                            AsyncRequestBody requestBody) {
        return s3AsyncClient().uploadPart(uploadPartRequest, requestBody);
    }

    @Override
    public CompletableFuture<UploadPartResponse> uploadPart(UploadPartRequest uploadPartRequest, Path sourcePath) {
        return s3AsyncClient().uploadPart(uploadPartRequest, sourcePath);
    }

    @Override
    public CompletableFuture<UploadPartResponse> uploadPart(Consumer<UploadPartRequest.Builder> uploadPartRequest,
                                                            Path sourcePath) {
        return s3AsyncClient().uploadPart(uploadPartRequest, sourcePath);
    }

    @Override
    public CompletableFuture<UploadPartCopyResponse> uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest) {
        return s3AsyncClient().uploadPartCopy(uploadPartCopyRequest);
    }

    @Override
    public CompletableFuture<UploadPartCopyResponse> uploadPartCopy(Consumer<UploadPartCopyRequest.Builder> uploadPartCopyRequest) {
        return s3AsyncClient().uploadPartCopy(uploadPartCopyRequest);
    }

    @Override
    public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, AsyncRequestBody requestBody) {
        return s3AsyncClient().writeGetObjectResponse(writeGetObjectResponseRequest, requestBody);
    }

    @Override
    public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(Consumer<WriteGetObjectResponseRequest.Builder> writeGetObjectResponseRequest, AsyncRequestBody requestBody) {
        return s3AsyncClient().writeGetObjectResponse(writeGetObjectResponseRequest, requestBody);
    }

    @Override
    public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(WriteGetObjectResponseRequest writeGetObjectResponseRequest, Path sourcePath) {
        return s3AsyncClient().writeGetObjectResponse(writeGetObjectResponseRequest, sourcePath);
    }

    @Override
    public CompletableFuture<WriteGetObjectResponseResponse> writeGetObjectResponse(Consumer<WriteGetObjectResponseRequest.Builder> writeGetObjectResponseRequest, Path sourcePath) {
        return s3AsyncClient().writeGetObjectResponse(writeGetObjectResponseRequest, sourcePath);
    }

    @Override
    public S3Utilities utilities() {
        return s3AsyncClient().utilities();
    }

    @Override
    public S3AsyncWaiter waiter() {
        return s3AsyncClient().waiter();
    }

    private static void validateOverrideConfiguration(AwsRequest request) {

        if (request.overrideConfiguration().isPresent()) {
            AwsRequestOverrideConfiguration overrideConfiguration = request.overrideConfiguration().get();
            if (overrideConfiguration.signer().isPresent()) {
                throw new UnsupportedOperationException("Request-level signer override is not supported");
            }

            // TODO: support request-level credential override
            if (overrideConfiguration.credentialsProvider().isPresent()) {
                throw new UnsupportedOperationException("Request-level credentials override is not supported");
            }
        }
    }

}
