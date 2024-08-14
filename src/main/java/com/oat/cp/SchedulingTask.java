package com.oat.cp;

import ilog.concert.IloException;

import java.util.Hashtable;


public abstract class SchedulingTask extends AbstractTask
{

    protected abstract boolean solve(String modelVersion) throws IloException;
    public void execute() {}
    /* seems not needed yet -by Sophia
    public final void executeInternal()
    {
        solve();
    }

    public SchedulingTask()
    {
        super();
        //_callback=callback;
    }

    private boolean _closed=false;
    public void close()
    {
        _closed=true;
        models=null;
    }
    public boolean isClosed() { return _closed; }


    private Hashtable models=new Hashtable();
    public Hashtable getModels() { return models; }
    public Model getModel(String modelId) { return (Model)models.get(modelId); }


    //protected TaskCallback _callback=null;
    protected boolean _prepared=false;

    public boolean prepare()
    {
        if (!_prepared)
        {
            //_prepared=_callback.prepare();
        }
        return _prepared;
    }

    public void standDown()
    {
        if (_prepared)
        {
            // _callback.standDown();
            _prepared=false;
        }
    }

    public interface TaskCallback
    {
        public boolean prepare();
        public void standDown();
    }

     */
}


