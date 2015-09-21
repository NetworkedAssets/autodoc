package com.networkedassets.autodoc.event;

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
        return "ClassPojo [fromHash = "+fromHash+", refId = "+refId+", type = "+type+", toHash = "+toHash+"]";
    }
}