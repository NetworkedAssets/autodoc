angular.module("DoC").directive("docClassDiagram", function($state, $http, urlService) {
    return {
        link: function(scope, element) {
            var dagreGraph, svg, inner, render;
            var scopeLockSize = 16;

            var init = function() {
                dagreGraph = new dagreD3.graphlib.Graph().setDefaultEdgeLabel(function() {
                    return {};
                });
                dagreGraph.setGraph({});
                svg = d3.select(element[0]).append("svg");
                inner = svg.append("g");
                svg.append("text")
                    .attr("opacity", 0)
                    .attr("class", "textSizer");
            };

            var drawEntity = function(entity) {

                dagreGraph.setNode(entity.qualified, {
                    data: {
                        abstract: entity.abstract,
                        static: entity.static,
                        methods: entity.methods,
                        name: entity.name,
                        qualified: entity.qualified,
                        fields: entity.fields,
                        type: entity.type
                    },
                    label: "",
                    width: entity.size.width,
                    height: entity.size.height,
                    shape: "class"
                });
            };

            scope.load = function() {
                var url;
                if (scope.qualified === true) {
                    url = urlService.getRestUrl("UML");
                } else {
                    url = urlService.getRestUrl("UML", scope.qualified);
                }
                $http
                    .get(url, {
                        cache: true
                    })
                    .then(function(response) {
                        generate(response.data);
                    });
            };

            var generate = function(data) {

                var entities = {};

                var processEntity = function(entity, recLevel) {
                    if (!recLevel) {
                        recLevel = 0;
                    }
                    if (recLevel > 10) {
                        throw "StackOverflow?";
                    }
                    if (typeof entity == "object") {
                        if (entity.type == "package") {

                            $.each(entity, function(key, value) {
                                processEntity(value, recLevel + 1);
                            });
                        } else {
                            entities[entity.qualified] = entity;
                        }
                    }
                };

                $.each(data.entities, function(key, entity) {
                    processEntity(entity);
                });

                $.each(entities, function(key, entity) {

                    entity.methods = entity.method;

                    if (!entity.methods) {
                        entity.methods = [];
                    }

                    entity.fields = entity.field;

                    entity.constructors = [];

                    if (typeof entity.constructor == "object") {
                        entity.methods = (entity.constructors.concat(entity.constructor).concat(entity.methods));
                    }


                    if (entity.methods) {
                        $.each(entity.methods, function(key, method) {
                            elementToString(method);
                        });
                    } else {
                        entity.methods = [];
                    }


                    if (entity.fields) {
                        $.each(entity.fields, function(key, field) {
                            elementToString(field);
                        });
                    } else {
                        entity.fields = [];
                    }

                    calculateEntityWidth(entity);
                    drawEntity(entity);


                });

                $.each(data.relations, function(key, relation) {
                    var type = relation.type;
                    dagreGraph.setEdge(relation.source, relation.target, {
                        //lineInterpolate: "step-after",
                        class: type,
                        arrowhead: type
                    });
                });

                var zoom = d3.behavior.zoom().on("zoom", function() {
                    inner.attr("transform", "translate(" + d3.event.translate + ")" +
                        "scale(" + d3.event.scale + ")");
                }).scaleExtent([0.1, 1]);
                svg.call(zoom);

                render = new dagreD3.render();

                render.arrows().aggregation = function(parent, id) {
                    var marker = parent.append("marker")
                        .attr("id", id)
                        .attr("viewBox", "0 0 50 50")
                        .attr("refX", 20)
                        .attr("refY", 10)
                        .attr("markerWidth", 20)
                        .attr("markerHeight", 20)
                        .attr("orient", "auto")
                        .classed("aggregation", true);

                    var polygon = marker.append("polygon")
                        .attr("points", "0,10 10,5 20,10 10,15");
                };

                render.arrows().composition = function(parent, id) {
                    var marker = parent.append("marker")
                        .attr("id", id)
                        .attr("viewBox", "0 0 50 50")
                        .attr("refX", 20)
                        .attr("refY", 10)
                        .attr("markerWidth", 20)
                        .attr("markerHeight", 20)
                        .attr("orient", "auto")
                        .classed("composition", true);

                    var polygon = marker.append("polygon")
                        .attr("points", "0,10 10,5 20,10 10,15");
                };

                render.arrows().association = function(parent, id, edge, type) {
                };

                render.arrows().dependency = function(parent, id) {
                    var marker = parent.append("marker")
                        .attr("id", id)
                        .attr("viewBox", "0 0 50 50")
                        .attr("refX", 20)
                        .attr("refY", 10)
                        .attr("markerWidth", 20)
                        .attr("markerHeight", 20)
                        .attr("orient", "auto")
                        .classed("composition", true);

                    var polygon = marker.append("polygon")
                        .attr("points", "10,5 20,10 10,15 19.9999,10");
                };

                render.arrows().realization = function(parent, id) {
                    var marker = parent.append("marker")
                        .attr("id", id)
                        .attr("viewBox", "0 0 50 50")
                        .attr("refX", 20)
                        .attr("refY", 10)
                        .attr("markerWidth", 20)
                        .attr("markerHeight", 20)
                        .attr("orient", "auto")
                        .classed("realization", true);

                    var polygon = marker.append("polygon")
                        .attr("points", "10,5 20,10 10,15");
                };

                render.arrows().generalization = function(parent, id) {
                    var marker = parent.append("marker")
                        .attr("id", id)
                        .attr("viewBox", "0 0 50 50")
                        .attr("refX", 20)
                        .attr("refY", 10)
                        .attr("markerWidth", 20)
                        .attr("markerHeight", 20)
                        .attr("orient", "auto")
                        .classed("composition", true);

                    var polygon = marker.append("polygon")
                        .attr("points", "10,5 20,10 10,15");
                };

                render.shapes().class = function(parent, bbox, node) {
                    var headerRows = 1;

                    if (node.data.type == "interface") {
                        headerRows++;
                    }

                    var rows = headerRows + node.data.methods.length + node.data.fields.length;


                    var w = bbox.width,
                        h = bbox.height;

                    var h_row = 1 / rows * h;
                    var h1 = headerRows * h_row;
                    var h2 = (headerRows + node.data.fields.length) / rows * h;

                    var g = parent.insert("g")
                        .attr("x", -w / 2)
                        .attr("y", -h / 2);

                    var outerRect = g.append("rect")
                        .attr("x", -w / 2)
                        .attr("y", -h / 2)
                        .attr("width", w)
                        .attr("height", h)
                        .attr("rx", 5)
                        .attr("ry", 5)
                        ;

                    var nameRect = g.append("rect")
                        .attr("x", -w / 2)
                        .attr("y", -h / 2)
                        .attr("width", w)
                        .attr("height", h1)
                        .attr("rx", 5)
                        .attr("ry", 5)
                        .classed("head", true)
                        ;


                    var textOffset = {
                        x: -w / 2 + 4,
                        y: -h / 2 + h_row / 2 + 4
                    };

                    if (node.data.type != "class") {

                        if (node.data.type == "interface") {


                            g.append("text")
                                .classed("head", true)
                                .attr("x", 0)
                                .attr("y", textOffset.y)
                                .attr("text-anchor", "middle")
                                .text('«Interface»')
                            ;
                        }

                        g.append("text")
                            .classed("head", true)
                            .attr("x", 0)
                            .attr("y", textOffset.y + h_row)
                            .attr("text-anchor", "middle")
                            .text(node.data.name)
                        ;
                    } else {
                        var headText = g.append("text")
                            .classed("head", true)
                            .attr("x", 0)
                            .attr("y", textOffset.y)
                            .attr("text-anchor", "middle")
                            .text(node.data.name)
                            ;
                        if (node.data.abstract) {
                            headText.classed("abstract", true);
                        }
                    }


                    if (node.data.fields && node.data.fields.length) {
                        var fieldRect = g.append("rect")
                            .attr("x", -w / 2)
                            .attr("y", -h / 2 + h1)
                            .attr("width", w)
                            .attr("height", h2 - h1)
                            ;
                        var i = 0;
                        node.data.fields.forEach(function(elem) {
                            var tspan = g.append("text")
                                //.classed("abstract",true)
                                //.classed("static",true)
                                .attr("x", textOffset.x + scopeLockSize)
                                .attr("y", textOffset.y + h_row * i + h1)
                                .append("tspan")
                                .text(elem.string)
                                ;
                            if (elem.abstract) {
                                tspan.classed("abstract", true);
                            }
                            if (elem.static) {
                                tspan.classed("static", true);
                            }
                            if (elem.scope == "public" || elem.scope == "private" || elem.scope == "protected") {
                                g.append("image")
                                    .attr("x", textOffset.x - 3)
                                    .attr("y", textOffset.y - 15 + h_row * i + h1)
                                    .attr("width", scopeLockSize)
                                    .attr("height", scopeLockSize)
                                    .attr("xlink:href", doc_resourcePath + "images/" + elem.scope + "ScopeLock.png");
                            }

                            i++;
                        });

                    }

                    if (node.data.methods) {
                        var i = 0;
                        node.data.methods.forEach(function(elem) {
                            var tspan = g.append("text")
                                //.classed("abstract",true)
                                //.classed("static",true)
                                .attr("x", textOffset.x + scopeLockSize)
                                .attr("y", textOffset.y + h_row * i + h2)
                                .append("tspan")
                                .text(elem.string)
                                ;
                            if (elem.abstract) {
                                tspan.classed("abstract", true);
                            }
                            if (elem.static) {
                                tspan.classed("static", true);
                            }

                            if (elem.scope == "public" || elem.scope == "private" || elem.scope == "protected") {
                                g.append("image")
                                    .attr("x", textOffset.x - 3)
                                    .attr("y", textOffset.y - 15 + h_row * i + h2)
                                    .attr("width", scopeLockSize)
                                    .attr("height", scopeLockSize)
                                    .attr("xlink:href", doc_resourcePath + "images/" + elem.scope + "ScopeLock.png");
                            }

                            i++;

                        });

                    }

                    node.intersect = function(point) {
                        return dagreD3.intersect.rect(node, point);
                    };

                    g.on("click", function() {
                        if (!d3.event.defaultPrevented) {
                            console.log(node.data.qualified);
                            $state.go("javadoc.entity", {
                                name: node.data.qualified
                            });
                        }
                    });

                    return outerRect;
                };

                render(inner, dagreGraph);

                var initialScale = Math.min(parseInt(svg.style("width")) / dagreGraph.graph().width, parseInt(svg.style("height")) / dagreGraph.graph().height) * 0.9;

                zoom
                    .scale(initialScale)
                    .translate([parseInt(svg.style("width")) / 2 - dagreGraph.graph().width * initialScale / 2, 20])
                    .event(svg);
            };

            var elementToString = function(elem) {
                var string = "";
                var isMethod = (typeof elem.return != "undefined") || elem.signature;
                switch (elem.scope) {
                    case "public":
                        string += "+ ";
                        break;
                    case "protected":
                        string += "# ";
                        break;
                    case "private":
                        string += "- ";
                        break;
                    default:
                        string += "~ ";
                }

                if (elem.final) {
                    string += "final ";
                }

                string += elem.name;

                if (isMethod) {
                    string += "(";
                    if (elem.parameter) {
                        elem.parameter.forEach(function(value, i) {
                            if (i > 0) {
                                string += ', ';
                            }


                            string += value.name + ": " + qName(value.type);
                        });
                    }


                    string += ")";
                }

                if (elem.return) {
                    elem.type = elem.return;
                }

                if (elem.type) {
                    string += ": " + qName(elem.type);
                }

                string = string.replace("\u0020", "\u00a0");
                elem.string = string;
                return elem;
            };

            var calculateEntityWidth = function(entity) {

                var getWidth = function(text) {
                    if (text) {
                        var width = 0;
                        svg.select(".textSizer").text(text).each(function() {
                            width = this.getBBox().width;
                            return false;
                        });
                        return width;
                    } else {
                        return 0;
                    }

                };

                var widest = 0;

                if (entity.fields) {
                    entity.fields.forEach(function(elem) {
                        var width = getWidth(elem.string);
                        if (width > widest) {
                            widest = width;
                        }
                    });
                }

                if (entity.methods) {
                    entity.methods.forEach(function(elem) {
                        var width = getWidth(elem.string);
                        if (width > widest) {
                            widest = width;
                        }
                    });
                }

                var width = getWidth(entity.name);
                if (width > widest) {
                    widest = width;
                }

                var methods = entity.methods ? entity.methods.length : 0;
                var fields = entity.fields ? entity.fields.length : 0;

                var headerRows = 1;
                if (entity.type != "class") {
                    headerRows++;
                }

                var verticalCount = (Math.max(methods, 1) + Math.max(fields, 1) + headerRows);

                entity.size = {
                    width: Math.max(widest + 50, 200),
                    height: verticalCount * 20
                };
            };

            init();

            scope.$watch("qualified", function(qualified) {
                if (qualified) {
                    scope.load();
                }
            });
        },
        scope: {
            qualified: "=docClassDiagram"
        }
    };
});