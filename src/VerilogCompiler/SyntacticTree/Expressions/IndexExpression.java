/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Expressions;

import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.VNode;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class IndexExpression extends PrimaryExpression {
    String identifier;
    Expression expression;

    public IndexExpression(String identifier, Expression expression, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.expression = expression;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", identifier, expression);
    }

    @Override
    public ExpressionType validateSemantics() {
        if (!SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not declared");
            return ExpressionType.ERROR;
        } else if (!SemanticCheck.getInstance().variableIsArrayOrVector(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is not a vector/array");
            return ExpressionType.ERROR;
        }
        ExpressionType result = expression.validateSemantics();
        if (result != ExpressionType.INTEGER &&
                result != ExpressionType.VECTOR) {
            ErrorHandler.getInstance().handleError(line, column, 
                    "index must be an integer");
            return ExpressionType.ERROR;
        }
        if (SemanticCheck.getInstance().variableIsArray(identifier) &&
                SemanticCheck.getInstance().variableIsVector(identifier))
            return type = ExpressionType.VECTOR;
        return type = ExpressionType.INTEGER;
    }
    
    @Override
    public ExpressionValue evaluate(SimulationScope simulationScope, String moduleName) {
        switch (type) {
            case ERROR: case INTEGER: case ARRAY: return null;
            case VECTOR:
                ExpressionValue complete = simulationScope.getVariableValue(moduleName, identifier);
                ExpressionValue min = expression.evaluate(simulationScope, moduleName);
                
                if (min.value != null) {                
                    Integer minLimit = Integer.parseInt(min.value.toString(), 2);

                    int realMin = adjustPositionToSize(minLimit, simulationScope, moduleName);

                    Object[] values = (Object[]) complete.value;

                    if (realMin < 0 || realMin > values.length)
                        /*ERROR!!!*/
                        return null;
                    return new ExpressionValue(values[realMin], 0);
                }
                return null;
            default: return null;
        }
    }
    
    public int adjustPositionToSize(int position, 
            SimulationScope simulationScope, String moduleName) {
        VariableInfo info = simulationScope.getVariableInfo(moduleName, identifier);
        if (position < Math.min(info.LSB, info.MSB) || position > Math.max(info.LSB, info.MSB)) {
            return Integer.MAX_VALUE;
        }
        if (info.isBigEndian) {
            int min = info.LSB;
            return position - min;
        } else {
            int max = info.LSB;
            return max - position;
        }
    }

    @Override
    public VNode getCopy() {
        IndexExpression copy = new IndexExpression(identifier, (Expression)expression.getCopy(), line, column);
        copy.type = type;
        return copy;
    }
}
