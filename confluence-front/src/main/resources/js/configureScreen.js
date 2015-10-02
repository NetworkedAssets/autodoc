/**
 * Created by mrobakowski on 9/18/2015.
 */
(function ($, undefined) {

    function post(path, params, method) {
        method = method || "post";

        var form = document.createElement("form");
        form.setAttribute("method", method);
        form.setAttribute("action", path);

        for ( var key in params ) {
            if (params.hasOwnProperty(key)) {
                var hiddenField = document.createElement("input");
                hiddenField.setAttribute("type", "hidden");
                hiddenField.setAttribute("name", key);
                hiddenField.setAttribute("value", params[key]);

                form.appendChild(hiddenField);
            }
        }

        document.body.appendChild(form);
        form.submit();
    }

    function toggleExpanded($div, time) {
        time = time || 200;
        var $header = $div.find("> .expandable-header");
        var $contents = $div.find("> .expandable-contents");
        $contents.slideToggle(time, function () {
            var $arrow = $header.find('.expand-arrow');
            $arrow.toggleClass('aui-iconfont-arrows-down');
            $arrow.toggleClass('aui-iconfont-arrows-up');
        });
    }

    function collapseAll() {
        toggleExpanded($(".expandable-contents:visible").parent());
    }

    function expandAll() {
        toggleExpanded($(".expandable-contents:hidden").parent());
    }

    function expandImportant(time) {
        var $activeBranches = $(".branch input.checkbox:checked, .branch .scheduled-event").parents(".branch");
        var $activeRepos = $activeBranches.parents(".repo");
        var $activeProjects = $activeRepos.parents(".project");

        toggleExpanded($activeProjects, time);
        toggleExpanded($activeRepos, time);
        toggleExpanded($activeBranches, time);
    }

    function saveScheduledEvents($branch) {
        var scheduledEvents = [];
        var $newScheduledEvents = $branch.find(".scheduled-event");
        var $scheduledEvent;
        for(var i = 0; $scheduledEvent = $($newScheduledEvents[i]), i < $newScheduledEvents.length; i++) {
            scheduledEvents[i] = {
                "scheduleStartIso": $scheduledEvent.find(".schedule-start input").val(),
                "periodIso": $scheduledEvent.find(".period input").val()
            }
        }
        return scheduledEvents;
    }

    function saveBranches($repo) {
        var branches = {};
        var $newBranches = $repo.find(".branch");
        var $branch;
        for (var i = 0; $branch = $($newBranches[i]), i < $newBranches.length; i++) {

            branches[$branch.data("branch-id")] = {
                "displayId": $branch.data("branch-display-id"),
                "id": $branch.data("branch-id"),
                "javadocPageId": $branch.find("select[name=javadoc]").val(),
                "umlPageId": $branch.find("select[name=uml]").val(),
                "isListened": $branch.find("input.isListened").is(":checked"),
                "scheduledEvents": saveScheduledEvents($branch)
            }
        }
        return branches;
    }

    function saveRepos($project) {
        var repos = {};
        var $newRepos = $project.find(".repo");
        var $repo;
        for (var i = 0; $repo = $($newRepos[i]), i < $newRepos.length; i++) {
            var branches = saveBranches($repo);
            repos[$repo.data("repo-slug")] = {
                "name": $repo.data("repo-name"),
                "slug": $repo.data("repo-slug"),
                "branches": branches
            }
        }
        return repos;
    }

    function saveSettings(eventData) {
        var newSettings = [];
        var $projects = $(".project");
        var $project;
        for(var i = 0; $project = $($projects[i]), i < $projects.length; i++) {
            var repos = saveRepos($project);

            newSettings[i] = {
                "name": $project.data("project-name"),
                "key": $project.data("project-key"),
                "repos": repos
            }
        }
        var settingsJSON = JSON.stringify(newSettings);
        console.log(settingsJSON);
        var payload = {
            newSettings: settingsJSON
        };
        if (eventData) {
            payload["event"] = eventData;
        }
        post('', payload);
    }

    AJS.toInit(function() {
        console.log("test");
        $(".expandable-contents").hide();
        expandImportant(0);
    });

    $(document).on('click', '.expandable-header', function (e) {
        var $el = $(this).parent();
        toggleExpanded($el);
    });

    $(document).on('click', '.add-scheduled-event-button', function (e) {
        $button = $(this);
        $(com.networkedassets.autodoc.configureGui.scheduledEventDetails({
            "scheduleStartIso": "",
            "periodIso": ""
        })).insertBefore($button);
    });

    $(document).on('click', '.remove-scheduled-event-button', function (e) {
        $(this).parent().parent().remove();
    });

    $(document).on('click', "#save-button",function() {
        console.log("clicked save");
        saveSettings();
        console.log("after save");
    });

    $(document).on("click", ".force-regenerate-button", function() {
        var projectKey = $(this).parents(".project").data("project-key");
        var repoSlug = $(this).parents(".repo").data("repo-slug");
        var branchId = $(this).parents(".branch").data("branch-id");
        var eventData = projectKey + "//" + repoSlug + "//" + branchId;
        saveSettings(eventData);
    });

})(AJS.$);