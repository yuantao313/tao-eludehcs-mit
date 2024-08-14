package com.oat.cp;

import java.util.Date;


/** Base class for all tasks. A task holds an
 *  optimization problem in the form of models. It also
 *  contains all the ConstraintTranslators.
 */

public interface Task
{
    public boolean isFailed();
    public boolean isStarted();
    public boolean isCompleted();
   // public String getUserId();

    /**
    * Locks any resource necessary
    */
    public boolean prepare();
    /**
    * Unlocks resources in the event that the task is cancelled
    */
    public void standDown();

    public void execute();
    public Date getCreationTime();

    public int getPercentComplete();

    public Date getCompletionTime();
    public Date getStartTime();
}


