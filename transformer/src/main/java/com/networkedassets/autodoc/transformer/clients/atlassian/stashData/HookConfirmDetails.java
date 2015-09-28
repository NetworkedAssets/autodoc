package com.networkedassets.autodoc.transformer.clients.atlassian.stashData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HookConfirmDetails {
  @Nonnull
  private String description;
  @Nonnull
  private String name;
  @Nullable
  private String configFormKey;
  @Nonnull
  private String type;
  @Nonnull
  private String key;
  @Nonnull
  private String version;

  @Nonnull
  public String getDescription() {
    return description;
  }

  @Nonnull
  public void setDescription(String description) {
    this.description = description;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public void setName(String name) {
    this.name = name;
  }

  @Nullable
  public String getConfigFormKey() {
    return configFormKey;
  }

  @Nullable
  public void setConfigFormKey(String configFormKey) {
    this.configFormKey = configFormKey;
  }

  @Nonnull
  public String getType() {
    return type;
  }

  @Nonnull
  public void setType(String type) {
    this.type = type;
  }

  @Nonnull
  public String getKey() {
    return key;
  }

  @Nonnull
  public void setKey(String key) {
    this.key = key;
  }

  @Nonnull
  public String getVersion() {
    return version;
  }

  @Nonnull
  public void setVersion(String version) {
    this.version = version;
  }

}
