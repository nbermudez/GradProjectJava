/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.ModuleItems;

import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Others.ModuleInstance;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ModuleInstantiation extends ModuleItem {
    String identifier;
    ArrayList<ModuleInstance> moduleInstanceList;

    public ModuleInstantiation(String identifier, ArrayList<ModuleInstance> moduleInstanceList, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.moduleInstanceList = moduleInstanceList;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<ModuleInstance> getModuleInstanceList() {
        return moduleInstanceList;
    }

    public void setModuleInstanceList(ArrayList<ModuleInstance> moduleInstanceList) {
        this.moduleInstanceList = moduleInstanceList;
    }

    @Override
    public String toString() {
        return String.format("%s %s;", identifier, 
                StringUtils.getInstance().ListToString(moduleInstanceList, ","));
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().moduleIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a valid module name");
            return ExpressionType.ERROR;
        }
        for (ModuleInstance moduleInstance : moduleInstanceList) {
            moduleInstance.setModuleName(identifier);
            moduleInstance.validateSemantics();
        }
        return null;
    }
    
    @Override
    public void initModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        /*TODO*/
        super.executeModuleItem(simulationScope, moduleInstanceId);
        for (ModuleInstance moduleInstance : moduleInstanceList) {
            moduleInstance.initModuleInstance(simulationScope);
        }
    }

    @Override
    public void executeModuleItem(SimulationScope simulationScope, String moduleInstanceId) {
        /*TODO*/
        super.executeModuleItem(simulationScope, moduleInstanceId);
        for (ModuleInstance moduleInstance : moduleInstanceList) {
            moduleInstance.executeModuleInstance(simulationScope, moduleInstanceId);
        }
    }

    @Override
    public VNode getCopy() {
        ArrayList<ModuleInstance> instances = new ArrayList<ModuleInstance>();
        for (ModuleInstance moduleInstance : moduleInstanceList) {
            instances.add((ModuleInstance)moduleInstance.getCopy());
        }
        return new ModuleInstantiation(identifier, instances, line, column);
    }
    
    
}
