angular.module("DoC")
    .directive("javadocAnnotation", function($sanitize, $compile) {
        var parseValue = function(argument, index) {
            if (!index) {
                index = 0;
            }
            if (argument.type.qualified === "java.lang.String") {
                return '"' + argument.value[index] + '"';
            } else if (argument.type.qualified === "java.lang.Class") {
                return '<span doc-qname="\'' + argument.value[index] + '\'"></span>.class';
            } else {
                return argument.value;
            }
        };

        return {
            link: function(scope, element) {
                var annotation = scope.javadocAnnotation;

                var hasArguments = function() {
                    return annotation.argument && angular.isArray(annotation.argument) && annotation.argument.length;
                };

                var isJustValue = function() {
                    return annotation.argument.length == 1 && annotation.argument[0].name === "value";
                };

                var html = '@<span doc-qname="javadocAnnotation"></span>';
                if (hasArguments()) {
                    html += "(";
                    if (isJustValue()) {
                        html += '<span>' + parseValue(annotation.argument[0]) + '</span>';
                    } else {
                        annotation.argument.forEach(function(argument) {
                            html += '<span doc-qname="\'' + argument.type.qualified + '\'" doc-visible-name="\'' + argument.name + '\'"></span> = ';
                            if (argument.value.length > 1) {
                                html += "{";
                                var i = 0;
                                argument.value.forEach(function(value, index) {
                                    html += parseValue(argument, index);
                                    if (++i < annotation.argument.length) {
                                        html += ", ";
                                    }
                                });
                                html += "}";
                            } else {
                                html += parseValue(argument);
                            }
                        });
                    }
                    html += ")";
                }
                element.html(html);
                $compile(element.contents())(scope);
            },
            scope: {
                "javadocAnnotation": "="
            }
        }
    });