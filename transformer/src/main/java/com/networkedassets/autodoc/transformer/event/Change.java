package com.networkedassets.autodoc.transformer.event;

import com.google.common.base.MoreObjects;

/**
 * Created by kamil on 21.09.2015.
 */

public class Change
{
    private String fromHash;
    private String refId;
    private String type;
    private String toHash;

    public String getFromHash ()
    {
        return fromHash;
    }

    public void setFromHash (String fromHash)
    {
        this.fromHash = fromHash;
    }

    public String getRefId ()
    {
        return refId;
    }

    public void setRefId (String refId)
    {
        this.refId = refId;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getToHash ()
    {
        return toHash;
    }

    public void setToHash (String toHash)
    {
        this.toHash = toHash;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this.getClass())
                .add("fromHash", fromHash)
                .add("refId", refId)
                .add("type", type)
                .add("toHash", toHash)
                .toString();
    }
}