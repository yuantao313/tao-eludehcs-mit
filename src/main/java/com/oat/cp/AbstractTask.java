
package com.oat.cp;

import java.util.Date;

public abstract class AbstractTask  implements Task
{
    public AbstractTask()
    {

    }
    public boolean isFailed()
    {
        return _failed;
    }

    public void setFailed(boolean failed)
    {
        _failed=failed;
    }

    public boolean isStarted()
    {
        return _startTime!=null;
    }

    public void setStarted()
    {
        _startTime=new Date();
    }

    public boolean isCompleted()
    {
        return _completionTime!=null;
    }

    public void setCompleted()
    {
        _completionTime=new Date();
    }
    
    public boolean prepare()
    {
        return true;
    }

    public void standDown()
    {
    }

    public Date getCreationTime()
    {
        return _creationTime;
    }

    public int getPercentComplete()
    {
        return isCompleted()?100:_percentComplete;
    }

    public void setPercentComplete(int percent)
    {
        _percentComplete=percent;
    }

    public Date getCompletionTime()
    {
        return _completionTime;
    }

    public Date getStartTime()
    {
        return _startTime;
    }

    private boolean _failed=false;
    private Date _startTime=null;
    private Date _completionTime=null;
    private int _percentComplete=0;

    private Date _creationTime=new Date();

}