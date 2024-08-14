package com.oat.cp;

import com.oat.common.utils.CheckDataUtil;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntervalVar;
import ilog.cp.IloCP;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * Base class for all CP models to be solved by the system.
 * Adds stuff for the raw CP model to be solved by CPLEX.
 */

@Log4j2
public abstract class CPModel extends Model {

    private Vector<Constraint> Constraints = new Vector<>();

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    //TODO: need to get time limit from plan_config - by Sophia
    //TODO: need to add CP model configurations here - by Sophia
    private int timeLimit = DEFAULT_PLAN_TIME_LIMIT;   // How long to search (seconds)

    private int conflictRefinerTimeLimit = DEFAULT_CONFLICT_REFINER_TIME_LIMIT; // the CPU time spent before terminating the conflict refiner.

    private int workers = DEFAULT_WORKERS; // the number of workers/threads to run in parallel to solve model


    //TODO: need chang to CP model solving status here - by Sophia
    public static final String SOLVING_STATUS_OPTIMAL = "Optimal solution found";
    public static final String SOLVING_STATUS_INFEASIBLE = "The model is infeasible";
    public static final String SOLVING_STATUS_UNBOUNDED = "The model is unbounded";
    public static final String SOLVING_STATUS_INFEASIBLEORUNBOUNDED = "The model is infeasible or unbounded";
    public static String SOLVING_STATUS_FEASIBLE = "Feasible solution found";
    public static String SOLVING_STATUS_UNKNOWN = "Status is unknown";

    private String solvingStatus = SOLVING_STATUS_UNKNOWN;


    protected final IloCP cp = new IloCP();
    public IloCP getCp() {
        return cp;
    }

    //TODO: see how we can use this - by Sophia
    private Vector<ConstraintViolationPlanResult> resultCheckEntities = new Vector<>();

    public CPModel(String id, SchedulingTask task) {
        super(id, task);
    }

    /**
     * Release resources used
     */
    @Override
    public void close() {
        super.close();
        Constraints = null; //TODO: added by Sophia, check if right - by Sophia
    }

    public void printNumVars(boolean printDetails) throws IloException {
        IloIntVar intVars[] = cp.getAllIloIntVars();
        log.info("Number of int variables is:  " + intVars.length);
        IloIntervalVar intervalVars[] = cp.getAllIloIntervalVars();
        log.info("Number of interval variables is  " + intervalVars.length);
        if (printDetails) {
            for (int i = 0; i < intVars.length && i < 20000; i++) {
                log.info("  var " + intVars[i].getName() + " index " + i + " min/max " + intVars[i].getMin() + "/" + intVars[i].getMax());
            }

            for (int i = 0; (i < intervalVars.length && i <= 20000); i++) {
                log.info("index " + i + " domain " + cp.getDomain(intervalVars[i]));
            }
        }

    }

    protected boolean solve() throws IloException {
        //变量数到15万以上，抓一次所有变量需要5分钟
//        int numberOfIntVariables = cp.getAllIloIntVars().length;
//        int numberOfIntervalVariables = cp.getAllIloIntervalVars().length;
//        log.info("Number of interval variables is: " + numberOfIntervalVariables);
        cp.setParameter(IloCP.DoubleParam.TimeLimit, timeLimit);
        cp.setParameter(IloCP.DoubleParam.ConflictRefinerTimeLimit,conflictRefinerTimeLimit);
        cp.setParameter(IloCP.IntParam.Workers, workers);
        log.info("排程时间限制为 " + timeLimit  + " 秒");
        log.info("排程使用线程限制为 " + workers  + " 个");
//        if (numberOfIntVariables > 0 || numberOfIntervalVariables > 0 ) {
            // return solveRemote();
            return solveLocal();
//        }
//        return true;
    }

    public void addConstraint(Constraint con) {
        Constraints.add(con);
    }

    protected boolean postConstraints() throws IloException {
        boolean errorFlag = false;
        for (Constraint con : Constraints) {
            System.out.println("Try to apply "+con.getClass());
            if (!con.apply()){
                errorFlag = true;
                return false;
            }
            //变量数到15万以上，抓一次所有变量需要5分钟
            //todo: comment this
            //this.printNumVars(true);
        }
        return true;
    }

    public void checkConstraints() {
        for (Constraint con : Constraints) {
            //System.out.println("Try to app "+con.getClass());
            this.addResultCheckErrorMessages(con.check());
            //this.printNumVars();
        }
        saveConstraintCheckResult();
    }
    abstract protected void saveConstraintCheckResult();
    
    //TODO: to be added according to our methods - by Sophia
    protected boolean solveLocal() throws IloException {
        boolean solve = false;
        //变量数到15万以上，抓一次所有变量需要5分钟
        //printNumVars(true);
        try {
            //TODO: solve the model
            //TODO: get variables and objective value back

            if (cp.solve()) {
                solve = true;
                //变量数到15万以上，抓一次所有变量需要5分钟
//                IloIntervalVar[] intervalVars = cp.getAllIloIntervalVars();
//                for (int i = 0; i < intervalVars.length; i++) {
//                    if (cp.isPresent(intervalVars[i]) ) {
//                        //if (!intervalVars[i].getName().contains("ivResourceAByDay")) {
//                            System.out.println(cp.getDomain(intervalVars[i]));
//                        //}
//                    }
//                }
//                IloIntVar[] intVars = cp.getAllIloIntVars();
//                for (int i = 0; i < intVars.length; i++){
//                    if (cp.getValue(intVars[i]) >0) {
//                        System.out.println(intVars[i].getName() + ": " + cp.getValue(intVars[i]));
//                    }
//                }
//                String[] kpiNames = cp.getAllKPINames();
//                for (int i = 0; i < kpiNames.length; i++){
//                    System.out.println(kpiNames[i] + ": " + cp.getKPIValue(kpiNames[i]));
//                }
            } else {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_NO_SOLUTION_FOUND,
                        "子模型 " + getId() + " " + MESSAGE_TYPE_NO_SOLUTION_FOUND);

                if (cp.refineConflict()){
                    cp.writeConflict();
                }
            }
        } catch (IloException e) {
            //TODO
            System.err.println("Error " + e );
            e.printStackTrace();
        }
        return solve;
    }

    //TODO: - by Sophia
    public String getSolvingStatus() {
        return solvingStatus;
    }

    public List<ConstraintViolationPlanResult> getResultCheckEntities() {
        return this.resultCheckEntities;
    }

    public void addResultCheckEntities(ConstraintViolationPlanResult entity) {
        this.resultCheckEntities.add(entity);
    }

    public void addResultCheckErrorMessages(Vector<ConstraintViolationPlanResult> entities) {
        this.resultCheckEntities.addAll(entities);
    }

    /**
     * One dimensional array of variables. Key is any Object that
     * implements the hashCode() and equals() methods.
     */
    public class Var1DArray {
        private HashMap varMap = new HashMap();
        private int varType;

        private String name;

        public Var1DArray(int varType, String name) {
            this.varType = varType;
            this.name = name;
        }

        public Object getVariable(Object key) {
            Object v = varMap.get(key);
            //todo added by Sophia: 这里注释掉是因为，对于expression或者function，
            // 可能无法判断什么时候是要创建，什么时候是要更新，需要更改返回null来判断
            /*
            if (v == null) {
                log.error(ConstantUtil.VARIABLE_NOT_DEFINED_ERROR);
                throw new UndefinedVariableException(this.getClass(), name, key);
            }

             */
            return v;
        }

        public void setVariable(Object key, Object v)
        {
            varMap.put(key, v);
        }
    }

    /**
     * Two dimensional array of variables. Key is (Object, Object) where
     * these objects must implement the hashCode() and equals() methods.
     */
    public class Var2DArray {
        private HashMap map = new HashMap();
        private int varType;
        private String name;

        public Var2DArray(int varType, String name) {
            this.varType = varType;
            this.name = name;
        }

        public final Object getVariable(Object key1,
                                              Object key2) {
            Var1DArray array1d = (Var1DArray) map.get(key1);
            if (array1d == null) {
                return null;
                //log.error(ConstantUtil.VARIABLE_NOT_DEFINED_ERROR);
                //throw new UndefinedVariableException(this.getClass(), name, key1);
            }
            return array1d.getVariable(key2);
        }
        public final void setVariable(Object key1, Object key2, Object v) {
            Var1DArray array1d = (Var1DArray) map.get(key1);
            if (array1d == null) {
                array1d = new Var1DArray(varType, name+"_per_"+key1);
                map.put(key1, array1d);
            }
            array1d.setVariable(key2, v);
        }
    }

    /**
     * Three dimensional array of variables. Key is (Object, Object, Object) where
     * these objects must implement the hashCode() and equals() methods.
     */

    public class Var3DArray{
        private HashMap map = new HashMap();

        private int varType;

        private String name;

        public Var3DArray(int varType, String name){
            this.varType = varType;
            this.name = name;
        }

        public final Object getVariable(Object key1,Object key2,Object key3){
                Var2DArray array2d = (Var2DArray) map.get(key1);
                if(array2d == null){
                    return null;
                    //log.error(ConstantUtil.VARIABLE_NOT_DEFINED_ERROR);
                    //throw new UndefinedVariableException(this.getClass(), name, key1);
                }
                return array2d.getVariable(key2,key3);
        }

        public final void setVariable(Object key1, Object key2, Object key3, Object v){
            Var2DArray array2d = (Var2DArray) map.get(key1);
            if(array2d == null){
                array2d = new Var2DArray(varType,name+"_per_"+key1);
                map.put(key1,array2d);
            }
            array2d.setVariable(key2,key3,v);

        }
    }

    /**
     * Four dimensional array of variables. Key is (Object, Object, Object, Object) where
     * these objects must implement the hashCode() and equals() methods.
     */
    public class Var4DArray {
        private HashMap map = new HashMap();

        private int varType;

        private String name;

        public Var4DArray(int varType, String name) {
            this.varType = varType;
            this.name = name;
        }

        public final Object getVariable(Object key1, Object key2, Object key3, Object key4) {
            Var3DArray array3d = (Var3DArray) map.get(key1);
            if (array3d == null) {
                return null;
                //log.error(ConstantUtil.VARIABLE_NOT_DEFINED_ERROR);
                //throw new UndefinedVariableException(this.getClass(), name, key1);
            }
            return array3d.getVariable(key2, key3, key4);
        }

        public final void setVariable(Object key1, Object key2, Object key3, Object key4, Object v) {
            Var3DArray array3d = (Var3DArray) map.get(key1);
            if (array3d == null) {
                array3d = new Var3DArray(varType, name + "_per_" + key1);
                map.put(key1, array3d);
            }
            array3d.setVariable(key2, key3, key4, v);

        }
    }

    /**
     * Five dimensional array of variables. Key is (Object, Object, Object, Object, Object) where
     * these objects must implement the hashCode() and equals() methods.
     */

        public class Var5DArray{
            private HashMap map = new HashMap();

            private int varType;

            private String name;

            public Var5DArray(int varType, String name){
                this.varType = varType;
                this.name = name;
            }

            public final Object getVariable(Object key1,Object key2,Object key3,Object key4,Object key5){
                Var4DArray array4d = (Var4DArray) map.get(key1);
                if(array4d == null){
                    return null;
                    //log.error(ConstantUtil.VARIABLE_NOT_DEFINED_ERROR);
                    //throw new UndefinedVariableException(this.getClass(), name, key1);
                }
                return array4d.getVariable(key2,key3,key4,key5);
            }

            public final void setVariable(Object key1, Object key2, Object key3, Object key4, Object key5, Object v){
                Var4DArray array4d = (Var4DArray) map.get(key1);
                if(array4d == null){
                    array4d = new Var4DArray(varType,name+"_per_"+key1);
                    map.put(key1,array4d);
                }
                array4d.setVariable(key2,key3,key4,key5,v);
            }

    }


    /**
     * Six dimensional array of variables. Key is (Object, Object, Object, Object, Object, Object) where
     * these objects must implement the hashCode() and equals() methods.
     */

    public class Var6DArray {
        private HashMap map = new HashMap();

        private int varType;

        private String name;

        public Var6DArray(int varType, String name) {
            this.varType = varType;
            this.name = name;
        }

        public final Object getVariable(Object key1, Object key2, Object key3, Object key4, Object key5, Object key6) {
            Var5DArray array5d = (Var5DArray) map.get(key1);
            if (array5d == null) {
                return null;
                //log.error(ConstantUtil.VARIABLE_NOT_DEFINED_ERROR);
                //throw new UndefinedVariableException(this.getClass(), name, key1);
            }
            return array5d.getVariable(key2, key3, key4, key5, key6);
        }

        public final void setVariable(Object key1, Object key2, Object key3, Object key4, Object key5, Object key6, Object v) {
            Var5DArray array5d = (Var5DArray) map.get(key1);
            if (array5d == null) {
                array5d = new Var5DArray(varType, name + "_per_" + key1);
                map.put(key1, array5d);
            }
            array5d.setVariable(key2, key3, key4, key5, key6, v);
        }

    }

}