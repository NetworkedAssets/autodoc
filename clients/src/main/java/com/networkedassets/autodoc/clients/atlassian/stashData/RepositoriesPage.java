package com.networkedassets.autodoc.clients.atlassian.stashData;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

public class RepositoriesPage {

	private Integer size;
	private Integer limit;
	private Boolean isLastPage;
	private List<Repository> values = new ArrayList<>();

	private Integer start;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Boolean getIsLastPage() {
		return isLastPage;
	}

	public void setIsLastPage(Boolean isLastPage) {
		this.isLastPage = isLastPage;
	}

	public List<Repository> getValues() {
		return values;
	}

	public void setValues(List<Repository> values) {
		this.values = values;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	
	@Override
	  public String toString() {
	    return MoreObjects.toStringHelper(this.getClass())
	        .add("size", size)
	        .add("limit", limit)
	        .add("isLastPage", isLastPage)
	        .add("values", values)
	        .add("start", start)
	        .toString();
	  }
}
