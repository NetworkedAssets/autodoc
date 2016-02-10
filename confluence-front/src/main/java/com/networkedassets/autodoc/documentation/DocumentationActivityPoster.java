package com.networkedassets.autodoc.documentation;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.atlassian.streams.thirdparty.api.Application;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.function.Consumer;

import static com.atlassian.streams.api.Html.html;
import static com.atlassian.streams.api.common.Option.some;

public class DocumentationActivityPoster implements Consumer<DocumentationAdded> {
    private final Application application;
    private final URI baseUrl;
    private ActivityService activityService;
    private Logger log = LoggerFactory.getLogger(DocumentationActivityPoster.class);

    public DocumentationActivityPoster(ApplicationProperties applicationProperties, ActivityService activityService) {
        this.activityService = activityService;
        baseUrl = URI.create(applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        application = Application.application(
                applicationProperties.getDisplayName() + " Documentation",
                baseUrl);
    }

    public void postDocumentationAdded(DocumentationAdded documentationAdded) {
        final Either<ValidationErrors, Activity> activity = buildPostDocumentationActivity(documentationAdded);

        postActivity(activity);
        logErrors(activity);
    }

    private void logErrors(Either<ValidationErrors, Activity> activity) {
        for (ValidationErrors ve : activity.left()) {
            log.error("Couldn't build activity:\n" + ve.toString());
        }
    }

    private void postActivity(Either<ValidationErrors, Activity> activity) {
        for (Activity a : activity.right()) {
            activityService.postActivity(a);
        }
    }

    private Either<ValidationErrors, Activity> buildPostDocumentationActivity(DocumentationAdded documentationAdded) {
        return Activity
                .builder(application, DateTime.now(), AuthenticatedUserThreadLocal.getUsername())
                .content(some(html(documentationAddedActivityContent(documentationAdded))))
                .title(some(html("Documentation added")))
                .url(some(baseUrl))
                .build();
    }

    private String documentationAddedActivityContent(DocumentationAdded documentationAdded) {
        return String.format("Added documentation for %s %s %s: %s",
                documentationAdded.getProject(),
                documentationAdded.getRepo(),
                documentationAdded.getBranch(),
                documentationAdded.getDocType()
        );
    }

    @Override
    public void accept(DocumentationAdded documentationAdded) {
        postDocumentationAdded(documentationAdded);
    }
}
