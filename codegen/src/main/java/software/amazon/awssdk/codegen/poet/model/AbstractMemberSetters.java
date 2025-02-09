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

package software.amazon.awssdk.codegen.poet.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static software.amazon.awssdk.codegen.poet.model.TypeProvider.ShapeTransformation.USE_BUILDER_IMPL;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
// CHECKSTYLE:OFF java.beans is required for services that use names starting with 'set' to fix bean-based marshalling.
import java.beans.Transient;
// CHECKSTYLE:ON
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import software.amazon.awssdk.codegen.model.intermediate.IntermediateModel;
import software.amazon.awssdk.codegen.model.intermediate.MemberModel;
import software.amazon.awssdk.codegen.model.intermediate.ShapeModel;
import software.amazon.awssdk.codegen.poet.PoetExtensions;
import software.amazon.awssdk.codegen.poet.model.TypeProvider.TypeNameOptions;
import software.amazon.awssdk.core.SdkBytes;

/**
 * Abstract implementation of {@link MemberSetters} to share common functionality.
 */
abstract class AbstractMemberSetters implements MemberSetters {
    protected final PoetExtensions poetExtensions;
    private final ShapeModel shapeModel;
    private final MemberModel memberModel;
    private final TypeProvider typeProvider;
    private final ServiceModelCopiers serviceModelCopiers;

    AbstractMemberSetters(IntermediateModel intermediateModel,
                          ShapeModel shapeModel,
                          MemberModel memberModel,
                          TypeProvider typeProvider) {
        this.shapeModel = shapeModel;
        this.memberModel = memberModel;
        this.typeProvider = typeProvider;
        this.serviceModelCopiers = new ServiceModelCopiers(intermediateModel);
        this.poetExtensions = new PoetExtensions(intermediateModel);
    }

    protected MethodSpec.Builder fluentAbstractSetterDeclaration(ParameterSpec parameter, TypeName returnType) {
        return fluentSetterDeclaration(parameter, returnType).addModifiers(Modifier.ABSTRACT);
    }

    protected MethodSpec.Builder fluentAbstractSetterDeclaration(String methodName,
                                                                 ParameterSpec parameter,
                                                                 TypeName returnType) {
        return setterDeclaration(methodName, parameter, returnType).addModifiers(Modifier.ABSTRACT);
    }


    protected MethodSpec.Builder fluentDefaultSetterDeclaration(ParameterSpec parameter, TypeName returnType) {
        return fluentSetterDeclaration(parameter, returnType).addModifiers(Modifier.DEFAULT);
    }

    protected MethodSpec.Builder fluentSetterBuilder(TypeName returnType) {
        return fluentSetterBuilder(memberAsParameter(), returnType);
    }

    protected MethodSpec.Builder fluentSetterBuilder(String methodName, TypeName returnType) {
        return fluentSetterBuilder(methodName, memberAsParameter(), returnType);
    }

    protected MethodSpec.Builder fluentSetterBuilder(ParameterSpec setterParam, TypeName returnType) {
        return fluentSetterBuilder(memberModel().getFluentSetterMethodName(), setterParam, returnType);
    }

    protected MethodSpec.Builder fluentSetterBuilder(String methodName, ParameterSpec setterParam, TypeName returnType) {
        return MethodSpec.methodBuilder(methodName)
                         .addParameter(setterParam)
                         .addAnnotation(Override.class)
                         .addAnnotations(maybeTransient(methodName))
                         .returns(returnType)
                         .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    }

    private Iterable<AnnotationSpec> maybeTransient(String methodName) {
        if (methodName.startsWith("set")) {
            return singleton(AnnotationSpec.builder(Transient.class).build());
        }

        return emptyList();
    }

    protected MethodSpec.Builder beanStyleSetterBuilder() {
        return beanStyleSetterBuilder(memberAsBeanStyleParameter(), memberModel().getBeanStyleSetterMethodName());
    }

    protected MethodSpec.Builder deprecatedBeanStyleSetterBuilder() {
        return beanStyleSetterBuilder(memberAsBeanStyleParameter(), memberModel().getDeprecatedBeanStyleSetterMethodName());
    }

    protected MethodSpec.Builder beanStyleSetterBuilder(ParameterSpec setterParam, String methodName) {
        return MethodSpec.methodBuilder(methodName)
                .addParameter(setterParam)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    }

    protected CodeBlock copySetterBody() {
        return copySetterBody("this.$1N = $2T.$3N($1N)", "this.$1N = $1N", serviceModelCopiers.copyMethodName());
    }

    protected CodeBlock fluentSetterWithEnumCollectionsParameterMethodBody() {
        return copySetterBody("this.$1N = $2T.$3N($1N)", "this.$1N = $1N",
                              serviceModelCopiers.enumToStringCopyMethodName());
    }


    protected CodeBlock copySetterBodyWithModeledEnumParameter() {
        return copySetterBody("this.$1N = $2T.$3N($1N)", "this.$1N = $1N",
                              serviceModelCopiers.enumToStringCopyMethodName());
    }

    protected CodeBlock copySetterBuilderBody() {
        if (memberModel.hasBuilder()) {
            return copySetterBody("this.$1N = $1N != null ? $2T.$3N($1N.build()) : null",
                                  "this.$1N = $1N != null ? $1N.build() : null",
                                  serviceModelCopiers.copyMethodName());
        }
        if (memberModel.containsBuildable()) {
            return copySetterBody("this.$1N = $2T.$3N($1N)", null, serviceModelCopiers.copyFromBuilderMethodName());
        }
        return copySetterBody();
    }

    protected CodeBlock beanCopySetterBody() {
        if (memberModel.isSdkBytesType()) {
            return sdkBytesSetter();
        }
        if (memberModel.isList() && memberModel.getListModel().getListMemberModel().isSdkBytesType()) {
            return sdkBytesListSetter();
        }
        if (memberModel.isMap() && memberModel.getMapModel().getValueModel().isSdkBytesType()) {
            return sdkBytesMapValueSetter();
        }

        return copySetterBuilderBody();
    }

    private CodeBlock sdkBytesSetter() {
        return CodeBlock.of("$1N($2N == null ? null : $3T.fromByteBuffer($2N));",
                            memberModel.getFluentSetterMethodName(), fieldName(),
                            SdkBytes.class);
    }

    private CodeBlock sdkBytesListSetter() {
        return CodeBlock.of("$1N($2N == null ? null : $2N.stream().map($3T::fromByteBuffer).collect($4T.toList()));",
                            memberModel.getFluentSetterMethodName(), fieldName(),
                            SdkBytes.class, Collectors.class);
    }

    private CodeBlock sdkBytesMapValueSetter() {
        return CodeBlock.of("$1N($2N == null ? null : " +
                            "$2N.entrySet().stream()" +
                            ".collect($4T.toMap(e -> e.getKey(), e -> $3T.fromByteBuffer(e.getValue()))));",
                            memberModel.getFluentSetterMethodName(), fieldName(),
                            SdkBytes.class, Collectors.class);
    }

    protected ParameterSpec memberAsParameter() {
        return ParameterSpec.builder(typeProvider.parameterType(memberModel), fieldName()).build();
    }

    protected ParameterSpec memberAsBeanStyleParameter() {
        TypeName type = typeProvider.typeName(memberModel, new TypeNameOptions().shapeTransformation(USE_BUILDER_IMPL)
                                                                                .useSubtypeWildcardsForCollections(true)
                                                                                .useCollectionForList(true)
                                                                                .useByteBufferTypes(true));
        return ParameterSpec.builder(type, fieldName()).build();
    }

    protected ShapeModel shapeModel() {
        return shapeModel;
    }

    protected MemberModel memberModel() {
        return memberModel;
    }

    protected String fieldName() {
        return memberModel.getVariable().getVariableName();
    }

    private MethodSpec.Builder fluentSetterDeclaration(ParameterSpec parameter, TypeName returnType) {
        return setterDeclaration(memberModel().getFluentSetterMethodName(), parameter, returnType);
    }

    private MethodSpec.Builder setterDeclaration(String methodName, ParameterSpec parameter, TypeName returnType) {
        return MethodSpec.methodBuilder(methodName)
                         .addModifiers(Modifier.PUBLIC)
                         .addParameter(parameter)
                         .returns(returnType);
    }

    private CodeBlock copySetterBody(String copyAssignment, String regularAssignment, String copyMethodName) {
        CodeBlock.Builder body = CodeBlock.builder();

        if (shapeModel.isUnion()) {
            body.addStatement("Object oldValue = this.$N", fieldName());
        }

        Optional<ClassName> copierClass = serviceModelCopiers.copierClassFor(memberModel);

        if (copierClass.isPresent()) {
            body.addStatement(copyAssignment, fieldName(), copierClass.get(), copyMethodName);
        } else {
            body.addStatement(regularAssignment, fieldName());
        }

        if (shapeModel.isUnion()) {
            body.addStatement("handleUnionValueChange(Type.$N, oldValue, this.$N)",
                              memberModel.getUnionEnumTypeName(),
                              fieldName());
        }

        return body.build();
    }
}
