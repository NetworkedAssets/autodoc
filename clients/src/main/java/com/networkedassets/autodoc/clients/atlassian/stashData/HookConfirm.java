package com.networkedassets.autodoc.clients.atlassian.stashData;

import javax.annotation.Nonnull;

public class HookConfirm {
  @Nonnull
  private Boolean enabled;

  @Nonnull
  private HookConfirmDetails details;

  @Nonnull
  private Boolean configured;

  @Nonnull
  public Boolean getEnabled() {
    return enabled;
  }

  @Nonnull
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @Nonnull
  public HookConfirmDetails getDetails() {
    return details;
  }

  @Nonnull
  public void setDetails(HookConfirmDetails details) {
    this.details = details;
  }

  @Nonnull
  public Boolean getConfigured() {
    return configured;
  }

  @Nonnull
  public void setConfigured(Boolean configured) {
    this.configured = configured;
  }

}