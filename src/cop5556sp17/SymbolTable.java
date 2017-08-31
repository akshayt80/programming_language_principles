package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import cop5556sp17.AST.Dec;


public class SymbolTable {

	static class SymAttrs{
		int scope_number;
		Dec dec;
	}
	
	Stack<Integer> scope_stack;
	int current_scope, next_scope;
	HashMap<String, ArrayList<SymAttrs>> symbolMap;
	ArrayList<SymAttrs> values;

	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		current_scope = next_scope++;
		scope_stack.push(current_scope);
	}

	/**
	 * leaves scope
	 */
	public void leaveScope(){
		scope_stack.pop();
		current_scope = scope_stack.peek();
	}
	
	public boolean insert(String ident, Dec dec){
		values = new ArrayList<>();
		if (symbolMap.containsKey(ident)){
			values = symbolMap.get(ident);
			for (SymAttrs attrs: values){
				int scope_number = attrs.scope_number;
				if (scope_number == current_scope){
					return false;
				}
			}
		}
		SymAttrs attributes = new SymAttrs();
		attributes.dec = dec;
		attributes.scope_number= current_scope;
		values.add(0, attributes);
		symbolMap.put(ident, values);
		return true;
	}
	
	public Dec lookup(String ident){
		if (symbolMap.containsKey(ident)){
			values = symbolMap.get(ident);
			for (SymAttrs attrs: values){
				int scope_number = attrs.scope_number;
				if (scope_stack.contains(scope_number)){
					return attrs.dec;
				}
			}
		}
		return null;
	}
		
	public SymbolTable() {
		scope_stack = new Stack<>();
		current_scope = 0;
		next_scope = 1;
		scope_stack.push(current_scope);
		symbolMap = new HashMap<>();
	}

	@Override
	public String toString() {
		String stack = scope_stack.toString();
		String hashMap = symbolMap.toString();
		return "Hashmap:" + hashMap + "\nScope Stack:" + stack;
	}
}
