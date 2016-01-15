angular.module("DoC")
    .directive('docGeneric', function ($compile, javadocEntities) {
        return {
            link: function (scope, element, attr) {
                var html = '';
                html += '&lt;';
                scope.docGeneric.forEach(function (generic, i) {
                    if (i > 0) {
                        html += '<span>, </span>';
                    }
                    //console.log(generic,generic.qualified);
                    html += '<span doc-qname="\'' + generic.qualified + '\'"></span>'
                    if (generic.wildcard) {
                        var processBound = function (bound, text) {
                            bound.forEach(function (bound, j) {
                                if (j > 0) {
                                    html += '<span>, </span>';
                                }
                                html += ' ' + text + ' ' + '<span doc-qname="\'' + bound.qualified + '\'"></span>';
                            });
                        };

                        if (generic.wildcard.extendsBound) {
                            processBound(generic.wildcard.extendsBound, "extends");
                        }
                        if (generic.wildcard.superBound) {
                            processBound(generic.wildcard.superBound, "super");
                        }
                    }
                });
                html += '&gt;';
                element.html(html);


                $compile(element.contents())(scope);
            },
            scope: {
                docGeneric: "="
            }
        };
    });