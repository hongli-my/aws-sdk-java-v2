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

package software.amazon.awssdk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * {@link SdkExtensionMethod} indicates a method that has been explicitly added to a service client interface on behalf of the
 * SDK. While most service clients are automatically and fully generated from the corresponding service's API model, the SDK may
 * sometimes choose to extend a service's logical API in order to provide improved abstractions or convenience functions. {@link
 * SdkExtensionMethod} implementations may invoke one or more service requests to fulfil their behavior.
 */
@Target(ElementType.METHOD)
@SdkProtectedApi
public @interface SdkExtensionMethod {
}
