angular.module("DoC")
    .directive('docQname', function($compile,javadocEntities) {
        return {
            link: function(scope,element,attr) {

                scope.name = qName(scope.source);
                scope.qualified = qName(scope.source,true);

                var isGeneric = false;

                if (typeof scope.source == "object" && scope.source.generic) {
                    isGeneric = true;
                }

                var html = '<span class="tooltip" ';
                if (javadocEntities.existsByName(scope.qualified)) {
                    html += 'ui-sref="javadoc.entity({name:qualified})"';
                    element.addClass("clickable");
                }

                html += '>{{name}}';
                if (isGeneric) {
                    html += '<span doc-generic="source.generic"></span>';
                }
                html += '</span>';
                element.html(html);

                if (typeof attr.noTooltip == "undefined") {
                    element.find("span.tooltip").tooltip({
                        gravity: 's',
                        title: function() {
                            return scope.qualified;
                        }
                    });
                }

                $compile(element.contents())(scope);

            },
            scope: {
                source: "=docQname",
                name: "=",
                qualified: "="
            }
        };
    });