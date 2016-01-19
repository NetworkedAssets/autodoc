function JavadocEntity(object) {
    var entity = this;

    this.scope = null;
    this.name = null;
    this.comment = null;
    this.generic = null;
    this.packageArray = [];
    this.elements = [];
    this.supers = [];
    this.interfaces = [];
    this.nestedClasses = [];
    this.nestedPackages = [];

    var parseModifiers = function(object) {
        if (object.modifier) {
            return object.modifier;
        } else {
            return [];
        }
    };

    var parseDetails = function(object) {
        var details = {
            tags: [],
            visible: false,
            exist: false
        };

        if (object.tag) {
            details.tags = object.tag;
            details.exist = true;
        }
        return details;
    };

    var parseElements = function(object) {
        entity.elements = [
            {
                type: "constructor",
                elements: (typeof object.constructor=="object")?object.constructor:[]
            },
            {
                type: "field",
                elements: object.field?object.field:[]
            },
            {
                type: "method",
                elements: object.method?object.method:[]
            }
        ];

        entity.elements.forEach(function(elementGroup) {
            if (typeof elementGroup.elements == "object") {
                elementGroup.elements.forEach(function(element) {
                    if (element.return) {
                        element.type = element.return;
                    }

                    if (typeof element.parameter == "undefined" && element.signature) {
                        element.parameter = [];
                    }

                    element.modifiers = parseModifiers(element);
                    element.details = parseDetails(element);
                });
            }
        });
    };

    this.parseFromObject = function(object) {
        entity.modifiers = parseModifiers(object);
        entity.type = object.type;
        entity.scope = object.scope;

        entity.comment = object.comment;
        entity.generic = object.generic;

        entity.qualified = object.qualified?object.qualified:object.name;

        entity.packageArray = entity.qualified.split(".");

        entity.name = entity.packageArray.pop();

        parseElements(object);

        if (typeof object.class == "object") {
            entity.supers = [object.class];
        }

        if (angular.isArray(object.interface)) {
            if (entity.type == "interface") {
                entity.supers = object.interface;
            } else {
                entity.interfaces = object.interface;
            }

        }

        if (angular.isArray(object.indexClass)) {
            entity.classes = object.indexClass;
        }

        if (object.children) {
            angular.forEach(object.children,function(child) {
                if (child.type == "package") {
                    entity.nestedPackages.push(child);
                } else {
                    entity.nestedClasses.push(child);
                }
            });
        }
    };

    if (typeof object == "object") {
        this.parseFromObject(object);
    }
}