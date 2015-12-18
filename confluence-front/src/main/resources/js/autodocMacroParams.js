/**
 * Created by mrobakowski on 12/17/2015.
 */
(function ($) {
    var jsOverrides = {
        "fields": {
            "enum": function (params, options) {
                if (params && params.name == "project") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
                    var $select = $("select", paramDiv);
                    // TODO: fetch all available projects and put them here
                    $(["a, b, c", "d, e, f"]).each(function () {
                        $select.append($("<option/>").attr("value", this).html("" + this));
                    });

                    var field = AJS.MacroBrowser.Field(paramDiv, $select, options);
                    return field;
                } else if (params && params.name == "repo") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
                    var $select = $("select", paramDiv);
                    // TODO: fetch repos and put them here
                    // TODO: should take chosen project into consideration
                    $(["a, b, c", "d, e, f"]).each(function () {
                        $select.append($("<option/>").attr("value", this).html("" + this));
                    });

                    var field = AJS.MacroBrowser.Field(paramDiv, $select, options);
                    return field;
                } else if (params && params.name == "project") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
                    var $select = $("select", paramDiv);
                    // TODO: fetch branches and put them here
                    // TODO: should take chosen repo into consideration
                    $(["a, b, c", "d, e, f"]).each(function () {
                        $select.append($("<option/>").attr("value", this).html("" + this));
                    });

                    var field = AJS.MacroBrowser.Field(paramDiv, $select, options);
                    return field;
                }

                return AJS.MacroBrowser.ParameterFields["enum"](params, options);
            }
        }
    };
    AJS.MacroBrowser.setMacroJsOverride("autodocDashboard", jsOverrides);
})(AJS.$);