(function($) {
    var base_url = $("meta#confluence-base-url").attr("content");
    var url = base_url + "/rest/doc/1.0/configuration/branches/listened";

    var vm = {
        chosen: {
            source: null,
            project: null,
            repo: null,
            branch: null
        },
        tree: null,
        macro: null
    };

    var projects = {
        select: null,
        projectInput: null,
        sourceInput: null,
        init: function() {
            var that = this;
            $(this.select)
                .empty()
                .append(processOptions(vm.tree.sources, $("<select/>")).contents())
                //.prepend('<option value="">-- choose --</option>')
                .prop("disabled", false);
            $(this.select).off("change.branchChooser").on("change.branchChooser", function() {
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
                    .append(processOptions(vm.tree.sources[vm.chosen.source].projects[vm.chosen.project].repos, $("<select/>")).contents())
                    //.prepend('<option value="">-- choose --</option>')
                    .prop("disabled", false);
            } else {
                $(this.select).empty().prop("disabled", true);
            }

            $(this.select).off("change.branchChooser").on("change.branchChooser", function() {
                that.set($(this).find("option:selected").val());
            }).change();
        },
        set: function(slug) {
            vm.chosen.repo = slug ? slug : null;
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
                    .append(processOptions(vm.tree.sources[vm.chosen.source].projects[vm.chosen.project].repos[vm.chosen.repo].branches, $("<select/>")).contents())
                    //.prepend('<option value="">-- choose --</option>')
                    .prop("disabled", false);
            } else {
                $(this.select).empty().prop("disabled", true);
            }
            this.reset();
            $(this.select).off("change.branchChooser").on("change.branchChooser", function() {
                that.set($(this).find("option:selected").val());
            }).change();
        },
        set: function(slug) {
            vm.chosen.branch = slug ? slug : null;
            enableOrDisableSaveButton();
        },
        reset: function() {
            this.set(null);
            disableSaveButton();
        }
    };

    var enableOrDisableSaveButton = function() {
        if ($("#doc_macroDialogOptions").find("input:checkbox:checked").length > 0 && vm.chosen.branch != null) {
            enableSaveButton();
        } else {
            disableSaveButton();
        }
    };

    var enableSaveButton = function() {
        $("#doc_macroDialogSaveButton").enable();
    };

    var disableSaveButton = function() {
        $("#doc_macroDialogSaveButton").disable();
    };

    var processOptions = function(data, elem) {
        $.each(data, function(key, v) {
            if (v.isOptGroup) {
                var optGroup = $('<optgroup/>', {
                    label: v.label
                });
                processOptions(v.options, optGroup);

                optGroup.appendTo(elem);
            } else {
                var attrs = {
                    val: v.value,
                    text: v.label
                };
                if (v.selected) {
                    attrs.selected = true;
                }
                var option = $('<option/>', attrs);

                option.appendTo(elem);
            }

        });
        return elem;
    };

    var processTree = function(raw) {
        vm.tree = {};
        vm.tree.sources = {};
        $.each(raw.sources, function(key, value) {
            var source = vm.tree.sources[key] = {
                value: key,
                label: value.name,
                isOptGroup: true,
                projects: {}
            };
            $.each(value.projects, function(key, value) {
                var project = source.projects[key] = {
                    value: source.value + '\uF000' + key,
                    label: value.name,
                    repos: {}
                };
                $.each(value.repos, function(key, value) {
                    var repo = project.repos[key] = {
                        value: key,
                        label: value.name,
                        branches: {}
                    };
                    $.each(value.branches, function(key, value) {
                        repo.branches[key] = {
                            value: value.id,
                            label: value.displayId
                        };
                    });
                });
            });
            source.options = source.projects;
        });
    };

    var dialogInstance = null;

    var save = function() {
        var macroName = "docMacro";

        var currentParams = $.extend({}, vm.chosen);
        currentParams.javadoc = $("#doc_macroDialogJavadocCheckbox").is(":checked");
        currentParams.classDiagram = $("#doc_macroDialogClassDiagramCheckbox").is(":checked");
        currentParams.structureGraph = $("#doc_macroDialogStructureGraphCheckbox").is(":checked");
        tinymce.confluence.macrobrowser.macroBrowserComplete({
            "name": macroName,
            "bodyHtml": undefined,
            "params": currentParams
        });
        AJS.dialog2(getDialogInstance()).hide();
    };

    var getDialogInstance = function() {
        if (dialogInstance === null) {
            var html =
                '<section role="dialog" id="doc_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">' +
                '   <header class="aui-dialog2-header">' +
                '       <h2 class="aui-dialog2-header-main">DoC macro parameters</h2>' +
                '       <a class="aui-dialog2-header-close">' +
                '           <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>' +
                '       </a>' +
                '   </header>' +
                '   <div class="aui-dialog2-content">' +
                '       <div id="doc_macroDialogError" style="text-align: center"  style="display:none">' +
                '           <span class="aui-icon aui-icon-wait aui-icon-large">Loading...</span>' +
                '           <div class="aui-message aui-message-error">' +
                '               <p class="title">' +
                '                   <strong>Unknown error occured</strong>' +
                '               </p>' +
                '               <p>Try refreshing the page.</p>' +
                '           </div>'+
                '       </div>' +
                '       <div style="display: table; width: 100%" id="doc_macroDialogContent">' +
                '           <div style="display: table-row">' +
                '               <div id="doc_macroDialogSelectCntr" style="display: table-cell; width: 47%; padding-right: 3%; border-right: 1px solid #ccc"></div>' +
                '               <div id="doc_macroDialogOptions" style="display: table-cell; width: 46%; padding-left: 3%;">' +
                '                   <h3>Components</h3>' +
                '                   <form class="aui">' +
                '                       <div class="field-group"><label for="doc_macroDialogJavadocCheckbox">Javadoc</label><div class="checkbox"><input type="checkbox" id="doc_macroDialogJavadocCheckbox"></div></div>' +
                '                       <div class="field-group"><label for="doc_macroDialogClassDiagramCheckbox">Class diagram</label><div class="checkbox"><input type="checkbox" id="doc_macroDialogClassDiagramCheckbox"></div></div>' +
                '                       <div class="field-group"><label for="doc_macroDialogStructureGraphCheckbox">Structure graph</label><div class="checkbox"><input type="checkbox" id="doc_macroDialogStructureGraphCheckbox"></div></div>' +
                '                   </form>' +
                '               </div>' +
                '          </div>' +
                '       </div>' +
                '   </div>' +
                '   <footer class="aui-dialog2-footer">' +
                '       <div class="aui-dialog2-footer-actions">' +
                '           <button id="doc_macroDialogSaveButton" class="aui-button aui-button-primary" disabled>Save</button>' +
                '           <button id="doc_macroDialogCloseButton" class="aui-button aui-button-link">Close</button>' +
                '       </div>' +
                '   </footer>' +
                '</section>';
            dialogInstance = $(html).appendTo("body");

            dialogInstance.find("#doc_macroDialogCloseButton").click(function() {
                AJS.dialog2("#doc_macroDialog").hide();
            });

            dialogInstance.find("#doc_macroDialogSaveButton").click(function() {
                save();
            });

            dialogInstance.find("#doc_macroDialogOptions input:checkbox").change(function() {
                enableOrDisableSaveButton();
            });

        }
        return dialogInstance;
    };

    var load = function() {
        if (vm.tree === null) {
            $("#doc_macroDialogLoading").fadeIn(0);
            $("#doc_macroDialogContent").fadeOut(0);
            $("#doc_macroDialogError").fadeOut(0);
            $.getJSON(url).then(function(data) {
                processTree(data);
                init();
            },function() {
                $("#doc_macroDialogError").fadeIn(0);
                $("#doc_macroDialogLoading").fadeOut(0);
            });
        } else {
            init();
        }
    };

    var init = function() {
        var cntr = $("#doc_macroDialogSelectCntr").empty().append('<h3>Branch</h3>');
        cntr.append("<label>Project</label><br>");
        projects.select = $('<select style="width: 200px"></select>').appendTo(cntr);
        cntr.append("<br><label>Repository</label><br>");
        repos.select = $('<select style="width: 200px"></select>').appendTo(cntr);
        cntr.append("<br><label>Branch</label><br>");
        branches.select = $('<select style="width: 200px"></select>').appendTo(cntr);

        projects.init();

        AJS.$(cntr.find("select")).auiSelect2();

        setParams();

        $("#doc_macroDialogLoading").fadeOut(0);
        $("#doc_macroDialogContent").fadeIn(0);
        enableOrDisableSaveButton();
    };

    var setParams = function() {
        if (vm.macro.params) {
            projects.select.select2("val", vm.macro.params.source + "\uF000" + vm.macro.params.project).change();
            repos.select.select2("val", vm.macro.params.repo).change();
            branches.select.select2("val", vm.macro.params.branch).change();

            // TODO refactor into a single array and couple foreach loops – for easier component addition in the future
            $("#doc_macroDialogJavadocCheckbox").prop("checked", vm.macro.params.javadoc === "true");
            $("#doc_macroDialogClassDiagramCheckbox").prop("checked", vm.macro.params.classDiagram === "true");
            $("#doc_macroDialogStructureDiagramCheckbox").prop("checked", vm.macro.params.structureGraph === "true");
        } else {
            $("#doc_macroDialogOptions").find("input:checkbox").prop("checked", true);
        }
    };

    AJS.MacroBrowser.setMacroJsOverride("docMacro", {
        "opener": function(macro) {
            vm.macro = macro;
            AJS.dialog2(getDialogInstance()).show();
            load();
        }
    });
})(AJS.$);