/**
 * Created by mrobakowski on 12/17/2015.
 */
(function ($) {
    // TODO Actual semi-dynamic, timeproof URL
    var url = "http://atlas.networkedassets.net/confluence/download/resources/com.networkedassets.autodoc.confluence-front:configuration-resources/configurationResources/data/settings3.json";

    var vm = {};

    vm.chosen = {
        source: null,
        project: null,
        repo: null,
        branch: null
    };

    vm.tree = {};

    /*
    * TODO Refactor – the whole parameter settings box should be moved to custom dialog
    * */

    var projects = {
        select: null,
        projectInput: null,
        sourceInput: null,
        init: function() {
            var that = this;
            $(this.select)
                .empty()
                .append(processOptions(vm.tree.sources,$("<select/>")).contents())
                .prepend('<option value="">-- choose --</option>')
            $(this.select).off("change.branchChooser").on("change.branchChooser",function() {
                that.set($(this).find("option:selected").val());
            }).change();
            repos.init();
        },
        set: function(slug) {
            if (slug) {
                var arr = slug.split("\uF000");
                vm.chosen.source = arr[0];
                vm.chosen.project = arr[1];
            } else {
                vm.chosen.source = null;
                vm.chosen.project = null;
            }
            $(this.sourceInput).val(vm.chosen.source);
            $(this.projectInput).val(vm.chosen.project);
            repos.init();
        }
    };

    var repos = {
        select: null,
        init: function() {
            var that = this;
            this.reset();
            if (vm.chosen.source && vm.chosen.project) {
                $(this.select)
                    .empty()
                    .append(processOptions(vm.tree.sources[vm.chosen.source].projects[vm.chosen.project].repos,$("<select/>")).contents())
                    .prepend('<option value="">-- choose --</option>')
                    .prop("disabled",false);
            } else {
                $(this.select).empty().prop("disabled",true);
            }

            $(this.select).off("change.branchChooser").on("change.branchChooser",function() {
                that.set($(this).find("option:selected").val());
            }).change();
        },
        set: function(slug) {
            vm.chosen.repo = slug?slug:null;
            branches.init();
        },
        reset: function() {
            this.set(null);
        }
    };

    var branches = {
        select: null,
        init: function() {
            var that = this;
            if (vm.chosen.repo) {
                $(this.select)
                    .empty()
                    .append(processOptions(vm.tree.sources[vm.chosen.source].projects[vm.chosen.project].repos[vm.chosen.repo].branches,$("<select/>")).contents())
                    .prepend('<option value="">-- choose --</option>')
                    .prop("disabled",false);
            } else {
                $(this.select).empty().prop("disabled",true);
            }
            this.reset();
            $(this.select).off("change.branchChooser").on("change.branchChooser",function() {
                that.set($(this).find("option:selected").val());
            }).change();
        },
        set: function(slug) {
            vm.chosen.branch = slug?slug:null;
        },
        reset: function() {
            this.set(null);
        }
    };


    var processOptions = function(data,elem) {
        $.each(data,function(key,v) {
            if (v.isOptGroup) {
                var optGroup =  $('<optgroup/>',{
                    label: v.label
                });
                processOptions(v.options,optGroup);

                optGroup.appendTo(elem);
            } else {
                var attrs = {
                    val: v.value,
                    text: v.label
                };
                if (v.selected) {
                    attrs.selected = true;
                }
                var option = $('<option/>',attrs);

                option.appendTo(elem);
            }

        });
        return elem;
    }

    
    var processTree = function(raw) {
        vm.tree = {};
        vm.tree.sources = {};
        $.each(raw.sources,function(key,value) {
            var source = vm.tree.sources[key] = {
                value: key,
                label: value.name,
                isOptGroup: true,
                projects: {}
            };
            $.each(value.projects,function(key,value) {
                var project = source.projects[key] = {
                    value: source.value+'\uF000'+key,
                    label: value.name,
                    repos: {}
                };
                $.each(value.repos,function(key,value) {
                    var repo = project.repos[key] = {
                        value: key,
                        label: value.name,
                        branches: {}
                    };
                    $.each(value.branches,function(key,value) {
                        repo.branches[key] = {
                            value: key,
                            label: value.displayId
                        };
                    });
                });
            });
            source.options = source.projects;
        });
    }


    var jsOverrides = {
        "fields": {
            "enum": function (params, options) {
                if (params && params.name == "sourceProject") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
                    var $select = $("select", paramDiv);

                    projects.select = $select;
                    $.getJSON(url).then(function(data) {
                        processTree(data);
                        projects.init();
                    });

                    var field = AJS.MacroBrowser.Field(paramDiv, $select, options);
                    return field;
                } else if (params && params.name == "repo") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
                    var $select = $("select", paramDiv);

                    repos.select = $select;

                    var field = AJS.MacroBrowser.Field(paramDiv, $select, options);
                    return field;
                } else if (params && params.name == "branch") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
                    var $select = $("select", paramDiv);

                    branches.select = $select;

                    var field = AJS.MacroBrowser.Field(paramDiv, $select, options);
                    return field;
                }

                return AJS.MacroBrowser.ParameterFields["enum"](params, options);
            },
            "string": function(params,options) {
                if (params && params.name == "source") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameter());
                    var input = $("input", paramDiv);
                    projects.sourceInput = input;
                    params.hidden = true;
                    var field = AJS.MacroBrowser.Field(paramDiv, input, options);
                    return field;
                } else if (params && params.name == "project") {
                    var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameter());
                    var input = $("input", paramDiv);
                    projects.projectInput = input;
                    params.hidden = true;
                    var field = AJS.MacroBrowser.Field(paramDiv, input, options);
                    return field;
                }
                return AJS.MacroBrowser.ParameterFields["string"](params, options);
            }
        }
    };
    AJS.MacroBrowser.setMacroJsOverride("autodocDashboard", jsOverrides);
})(AJS.$);