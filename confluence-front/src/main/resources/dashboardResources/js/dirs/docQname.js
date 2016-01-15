angular.module("DoC")
    .directive('docQname', function($compile,javadocEntities) {
        return {
            link: function(scope,element,attr) {
                /*
                * TODO Generics
                * TODO Aui label instead of plain title attr
                * */

                scope.name = qName(scope.source);
                scope.qualified = qName(scope.source,true);

                var isGeneric = false;

                if (typeof scope.source == "object" && scope.source.generic) {
                    isGeneric = true;
                }

                var html = '<span title="{{qualified}}"';
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

                $compile(element.contents())(scope);

            },
            scope: {
                source: "=docQname",
                name: "=",
                qualified: "="
            }
        };
    });