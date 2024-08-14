
package com.oat.cp;

import com.oat.patac.engine.PatacSchedulingTask;
import ilog.concert.IloException;
import lombok.Data;

import java.util.Date;


/** Base class for all models to be solved by the system.
 *  The model is responsible for getting any information
 *  it needs via the Task, accessing the objectserver
 *  as necessary.
 *  The model is responsible for solving the problem
 *  and getting back the results.
 *  The model is resposible for interpretting the results, cooking
 *  them into a form ready for the next model or putting the results
 *  back into the objectserver.
 */
@Data
abstract public class Model
{
    private final String id;
    private final SchedulingTask task;


    public Model(String id, SchedulingTask task)
    {
        this.id=id;
        this.task=task;
    }

    //public String getId() { return id; }
    //public SchedulingTask getTask() { return task; }

    public boolean solveModel() throws IloException {
        Date st = new Date();
        if(!postConstraints()){
         return false;
        }
        addObjective();

        if (solve())
        {
//            checkConstraints();
//            processResults();
            return true;
        }
        return false;
    }

    abstract protected boolean postConstraints() throws IloException;
    abstract  protected  void checkConstraints();

    /**  Solve the model and return true if solve was successful.
     */
    abstract protected boolean solve() throws IloException;

    /** Cook the results and store them in the Model (or ObjectServer)
     *  Subsequent Models may access this model to get these results.
     */
    abstract protected void processResults() throws IloException;

    abstract protected void addObjective() throws IloException;

    private boolean closed=false;
    public void close() { closed=true; }
    //public boolean isClosed() { return closed; }

}
