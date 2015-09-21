/**
 * Created by mrobakowski on 9/18/2015.
 */
(function ($, undefined) {

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

    AJS.toInit(function() {
        console.log("test");
        $(".expandable-contents").hide();
        expandImportant(0);
    });

    $(document).on('click', '.expandable-header', function (e) {
        var $el = $(this).parent();
        toggleExpanded($el);
    });

    function collapseAll() {
        toggleExpanded($(".expandable-contents:visible").parent());
    }

    function expandAll() {
        toggleExpanded($(".expandable-contents:hidden").parent());
    }

    function expandImportant(time) {
        var $activeBranches = $(".branch input.checkbox:checked").parents(".branch");
        var $activeRepos = $activeBranches.parents(".repo");
        var $activeProjects = $activeRepos.parents(".project");

        toggleExpanded($activeProjects, time);
        toggleExpanded($activeRepos, time);
        toggleExpanded($activeBranches, time);
    }


})(AJS.$);