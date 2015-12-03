/**
 * Created by Jakub on 10/11/15.
 */

/* From JSON to Dagre */

var doc_resourcePath = AJS.Data.get("base-url")+"/" + AJS.Meta.get("dashboardResourcePath");

function ClassDiagram(options) {

    var defaults = {
        zoom: {
            min: 0.5,
            max: 3
        }
    };

    var settings;

    var dagreGraph;

    var paper;

    var scale = 1.0;

    var controlsDiv;

    var that = this;

    var svg, inner;

    var render;

    var constructor = function() {
        settings = $.extend({},defaults,options);
        var el = settings.elem;

        dagreGraph = new dagreD3.graphlib.Graph().setDefaultEdgeLabel(function() { return {}; });
        dagreGraph.setGraph({});

        svg = d3.select("svg");
        inner = svg.select("g");


        //controlsDiv = $('<div class="controls"><span class="zoomOut">Zoom -</span><span class="zoomIn">Zoom +</span><span class="fit">Fit content</span></div>').prependTo(paper.el);

    }

    constructor(arguments);

    /* Methods */

    this.setElement = function(elem) {
        settings.elem = $(elem);
        that.load();
    };

    this.load = function() {
        $.getJSON(doc_resourcePath + "uml.json?",function(data) {
            that.generate(data);
        });
    };

    var offsetToLocalPoint = function(offsetX, offsetY) {
        var svgPoint = paper.svg.createSVGPoint();
        svgPoint.x = offsetX;
        svgPoint.y = offsetY;
        var offsetTransformed = svgPoint.matrixTransform(paper.viewport.getCTM().inverse());
        return offsetTransformed;
    }

    this.zoom = function(step,ox,oy) {
        step*=0.1;
        scale += step;

        if (scale < settings.zoom.min) {
            scale = settings.zoom.min;
        } else if (scale > settings.zoom.max) {
            scale = settings.zoom.max;
        }

        var lp = offsetToLocalPoint(ox,oy);
        if (typeof ox != "undefined" && typeof oy != "undefined") {
            paper.setOrigin(0,0);
            paper.scale(scale,scale,ox,oy);
        } else {
            paper.scale(scale,scale);
        }


    }

    this.fitContent = function() {
        //paper.scaleContentToFit({
        //    padding: 0
        //});
        //var s = V(paper.viewport).scale();
        //scale = s.sx;
    }

    var initInteraction = function() {
        controlsDiv.find("span.zoomIn").click(function() {
            that.zoom(1);
        });
        controlsDiv.find("span.zoomOut").click(function() {
            that.zoom(-1);
        });
        controlsDiv.find("span.fit").click(function() {
            that.fitContent();
        });
        controlsDiv.fadeOut(0);
    }

    var drawEntity = function(entity) {

        dagreGraph.setNode(entity.qualified,{
            data: {
                abstract: entity.abstract,
                static: entity.static,
                methods: entity.methods,
                name: entity.name,
                fields: entity.fields,
                type: entity.type
            },
            label: "",
            width: entity.size.width,
            height: entity.size.height,
            shape: "class"
        });
    }

    var calculateEntityWidth = function(entity) {

        var getWidth = function(text) {
            if (text) {
                var width = 0;
                svg.select("#textSizer").text(text).each(function() {
                    width = this.getBBox().width;
                    return false;
                });
                return width;
            } else {
                return 0;
            }

        }

        var widest = 0;

        if (entity.fields) {
            entity.fields.forEach(function(elem,key) {
                var width = getWidth(elem.string);
                if (width > widest) {
                    widest = width;
                }
            });
        }

        if (entity.methods) {
            entity.methods.forEach(function(elem,key) {
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

        var methods = entity.methods?entity.methods.length:0;
        var fields= entity.fields?entity.fields.length:0;

        var headerRows = 1;
        if (entity.type != "class") {
            headerRows++;
        }

        var verticalCount = (Math.max(methods,1)+Math.max(fields,1)+headerRows);

        entity.size = {
            width: Math.max(widest+50,200),
            height: verticalCount*20
        };
    };

    /**
     * Creates method/attribute string in UML format from object
     * @param {Object} elem Attribute or Method object
     * @param {string} elem.visibility of elem
     * @param {bool} elem.static
     * @param {bool} elem.abstract
     * @param {bool} elem.type Type of attribute (attribute only – distinguishes attribute from method)
     * @param {bool} elem.returns Returned value's type (method only - distinguishes method from attribute)
    * */
    var elementToString = function(elem) {
        var string = "";
        var isMethod = (typeof elem.return != "undefined");
        switch (elem.scope) {
            case "public": string += "+ "; break;
            case "protected": string += "# "; break;
            case "private": string += "- "; break;
            default: string += "~ ";
        }

        if (elem.final) {
            string += "final ";
        }

        string += elem.name;

        if (isMethod) {
            string += "(";
            if (elem.parameter) {
                elem.parameter.forEach(function(value,i) {
                    if (i > 0) {
                        string += ', ';
                    }


                    string += value.name + ": "+qName(value.type);
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

        string = string.replace("\u0020","\u00a0");
        elem.string = string;
        return elem;
    }

    this.generate = function(data) {

        var entities = {};

        var processEntity = function(entity,recLevel) {
            if (!recLevel) {
                recLevel = 0;
            }
            if (recLevel > 10) {
                throw "StackOverflow?";
            }
            if (typeof entity == "object") {

                //if (entity.qualified == "com.networkedassets.autodoc.configureGui.ConfigureServlet") {
                //    console.log(entity.type == "package",entity.type,entity.name,entity.qualified);
                //}


                if (entity.type == "package") {

                    $.each(entity,function(key,value) {
                        processEntity(value,recLevel+1);
                    });
                } else {
                    entities[entity.qualified] = entity;
                }
            }
        }
        //console.log(data.entities);
        $.each(data.entities,function(key,entity) {
            processEntity(entity);
        });

        $.each(entities,function(key,entity) {


            if (entity.methods) {
                $.each(entity.methods,function(key,method) {
                    elementToString(method);
                });
            } else {
                entity.methods = [];
            }

            if (entity.fields) {
                $.each(entity.fields,function(key,field) {
                    elementToString(field);
                });
            } else {
                entity.fields = [];
            }

            calculateEntityWidth(entity);
            drawEntity(entity);





        });

        $.each(data.relations,function(key,relation) {
            dagreGraph.setEdge(relation.source,relation.target);
        });

        var zoom = d3.behavior.zoom().on("zoom", function() {
            inner.attr("transform", "translate(" + d3.event.translate + ")" +
                "scale(" + d3.event.scale + ")");
        });
        svg.call(zoom);

        // Create the renderer
        render = new dagreD3.render();

        render.shapes().class = function(parent, bbox, node) {
            //console.log(node);

            var headerRows = 1;

            if (node.data.type == "interface") {
                headerRows++;
            }

            var rows = headerRows+node.data.methods.length+node.data.fields.length;
            

            var w = bbox.width,
                h = bbox.height;

            var h_row = 1/rows*h;
            var h1 = headerRows*h_row;
            var h2 = (headerRows+node.data.fields.length)/rows*h;

            var g = parent.insert("g")
                .attr("x",-w/2)
                .attr("y",-h/2);

            var outerRect = g.append("rect")
                .attr("x",-w/2)
                .attr("y",-h/2)
                .attr("width",w)
                .attr("height",h)
                ;

            var nameRect = g.append("rect")
                .attr("x",-w/2)
                .attr("y",-h/2)
                .attr("width",w)
                .attr("height",h1)
                .classed("head",true)
                ;



            var textOffset = {
                x: -w/2+4,
                y: -h/2+h_row/2+4
            };

            if (node.data.type != "class") {

                if (node.data.type == "interface") {


                    g.append("text")
                        .classed("head",true)
                        .attr("x",0)
                        .attr("y",textOffset.y)
                        .attr("text-anchor","middle")
                        .text('«Interface»')
                    ;
                }

                g.append("text")
                    .classed("head",true)
                    .attr("x",0)
                    .attr("y",textOffset.y+h_row)
                    .attr("text-anchor","middle")
                    .text(node.data.name)
                    ;
            } else {
                var headText = g.append("text")
                    .classed("head",true)
                    .attr("x",0)
                    .attr("y",textOffset.y)
                    .attr("text-anchor","middle")
                    .text(node.data.name)
                    ;
                if (node.data.abstract) {
                    headText.classed("abstract",true);
                }
            }



            if (node.data.fields && node.data.fields.length) {
                var fieldRect = g.append("rect")
                    .attr("x",-w/2)
                    .attr("y",-h/2+h1)
                    .attr("width",w)
                    .attr("height",h2-h1)
                    ;
                var i = 0;
                node.data.fields.forEach(function(elem,index) {
                    var tspan = g.append("text")
                        //.classed("abstract",true)
                        //.classed("static",true)
                        .attr("x",textOffset.x)
                        .attr("y",textOffset.y+h_row*i+h1)
                        .append("tspan")
                        .text(elem.string)
                    ;
                    if (elem.abstract) {
                        tspan.classed("abstract",true);
                    }
                    if (elem.static) {
                        tspan.classed("static",true);
                    }
                    i++;
                });

            }

            if (node.data.methods) {
                var i = 0;
                node.data.methods.forEach(function(elem,index) {
                    var tspan = g.append("text")
                        //.classed("abstract",true)
                        //.classed("static",true)
                        .attr("x",textOffset.x)
                        .attr("y",textOffset.y+h_row*i+h2)
                        .append("tspan")
                        .text(elem.string)
                    ;
                    if (elem.abstract) {
                        tspan.classed("abstract",true);
                    }
                    if (elem.static) {
                        tspan.classed("static",true);
                    }
                    i++;

                });

            }

            var maxWidth = 0;

            node.intersect = function(point) {
                return dagreD3.intersect.rect(node, point);
            };

            return outerRect;
        };

        // Run the renderer. This is what draws the final graph.
        render(inner, dagreGraph);

        // Center the graph
        var initialScale = 0.75;
        zoom
            //.translate([(svg.attr("width") - dagreGraph.graph().width * initialScale) / 2, 20])
            .translate([-200, -400])
            .scale(initialScale)
            .event(svg);
        //svg.attr('height', dagreGraph.graph().height * initialScale + 40);






        /*dagreGraph.nodes().forEach(function(id) {

            var node = dagreGraph.node(id);
            var entity = entities[id];

            console.log(entities,id,entity,node);
            if (!entity) {
                console.log(id,node);
            }

            entity.position = {
                x: node.x,
                y: node.y
            };

            entity.attrs = {
                '*': {
                    "font-family": "Helvetica Neue",
                    "font-size": "12px"
                }
            };

            entity.methods = entity.methodString;
            entity.attributes = entity.fieldString;

            try {
                if (entity.type == "interface") {
                    entity.umlEntity = new uml.Interface(entity);
                } else if (entity.type == "abstract") {
                    entity.umlEntity = new uml.Abstract(entity);
                } else if (entity.type == "class") {
                    entity.umlEntity = new uml.Class(entity);
                } else {
                    throw "Invalid entity.type: "+entity.type;
                }
                graph.addCell(entity.umlEntity);
            } catch(e) {
                console.error(e);
            }



        });

        $.each(data.relations,function(key,relation) {
            var umlRelation = relation.umlRelation;
            var obj = {
                source: {
                    id: entities[relation.source].umlEntity.id
                },
                target: {
                    id: entities[relation.target].umlEntity.id
                }
            };

            if (relation.type == "implementation") {
                umlRelation = new uml.Implementation(obj);
            } else if (relation.type == "generalization") {
                umlRelation = new uml.Generalization(obj);
            } else if (relation.type == "association") {
                umlRelation = new uml.Association(obj);
            } else if (relation.type == "composition") {
                umlRelation = new uml.Composition(obj);
            } else if (relation.type == "aggregation") {
                umlRelation = new uml.Aggregation(obj);
            } else {
                umlRelation = new uml.Association(obj);
                console.error("Invalid relation.type: "+relation.type);
                //throw new String("Invalid relation.type: "+relation.type);
            }

            graph.addCell(umlRelation);

        });*/

        //initInteraction();
        //that.fitContent();
    };

};

/*
var cd = new ClassDiagram();
cd.load();

$(window).on("resize.ClassDiagram",function() {
    cd.setDimensions($(window).width(),$(window).height());
});


var method = {
    name: "doSomething",
    visibility: "public",
    arguments: [
        {
            type: "Object",
            name: "arg1"
        },
        {
            final: true,
            type: "String[]",
            name: "stringArr"
        }
    ],
    static: true,
    returns: "void"
};

console.log(cd.elementToString(method,true));
    */