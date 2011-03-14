package org.msgpack.template.builder;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.msgpack.template.JavassistTemplateBuilder;
import org.msgpack.template.ReflectionTemplateBuilder;


public class BuilderSelectorRegistry {
	
	private static BuilderSelectorRegistry instance = new BuilderSelectorRegistry();
	static{
		initForJava();
	}
	
	public static BuilderSelectorRegistry getInstance(){
		return instance;
	}
	
	TemplateBuilder forceBuilder;
	
	
    List<BuilderSelector> builderSelectors = new LinkedList<BuilderSelector>();

	private BuilderSelectorRegistry(){
	}
	private static void initForJava(){

		instance.append(new ArrayTemplateBuilderSelector());
		try {
			// FIXME JavassistTemplateBuilder doesn't work on DalvikVM
			if(System.getProperty("java.vm.name").equals("Dalvik")) {
				instance.append(
						new MessagePackMessageTemplateSelector(
								new ReflectionTemplateBuilder()));
				instance.forceBuilder = new ReflectionTemplateBuilder();
			}else{
				instance.append(
						new MessagePackMessageTemplateSelector(
								new JavassistTemplateBuilder()));
				instance.forceBuilder = new JavassistTemplateBuilder();
			}
		} catch (Exception e) {
			instance.append(
					new MessagePackMessageTemplateSelector(
							new JavassistTemplateBuilder()));
			instance.forceBuilder = new JavassistTemplateBuilder();
		}

		instance.append(new MessagePackOrdinalEnumBuilderSelector());
		instance.append(new EnumBuilderSelector());
	}
	
    
    public boolean contains(String builderSelectorName){
    	for(BuilderSelector bs : builderSelectors){
    		if(bs.getName().equals(builderSelectorName)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void append(BuilderSelector builderSelector){
    	
    	if(contains(builderSelector.getName())){
			throw new RuntimeException("Duplicate BuilderSelector name:" + builderSelector.getName());
    	}
    	this.builderSelectors.add(builderSelector);
    }
    public void prepend(BuilderSelector builderSelector){
    	if(contains(builderSelector.getName())){
			throw new RuntimeException("Duplicate BuilderSelector name:" + builderSelector.getName());
    	}
    	if(builderSelectors.size() > 0){
    		this.builderSelectors.add(0, builderSelector);
    	}else{
    		this.builderSelectors.add(builderSelector);
    	}
    }
    
    public void insert(int index,BuilderSelector builderSelector){
    	if(contains(builderSelector.getName())){
			throw new RuntimeException("Duplicate BuilderSelector name:" + builderSelector.getName());
    	}
    	if(builderSelectors.size() > 0){
    		this.builderSelectors.add(index, builderSelector);
    		
    	}else{
    		this.builderSelectors.add(builderSelector);
    	}
    }
    
    public void replace(BuilderSelector builderSelector){
    	String name = builderSelector.getName();
    	int index = getIndex(name);
    	builderSelectors.add(index, builderSelector);
    	builderSelectors.remove(index + 1);
    }
    
    public void insertBefore(String builderSelectorName,BuilderSelector builderSelector){
    	int index = getIndex(builderSelectorName);
    	
    	builderSelectors.add(index,builderSelector);
    }
    public void insertAfter(String builderSelectorName,BuilderSelector builderSelector){
    	int index = getIndex(builderSelectorName);
    	if(index + 1 == builderSelectors.size()){
    		builderSelectors.add(builderSelector);
    	}else{
    		builderSelectors.add(index + 1 , builderSelector);
    	}
    }
    private int getIndex(String builderSelectorName){
    	int index = 0;
    	for(BuilderSelector bs : builderSelectors){
    		if(bs.getName().equals(builderSelectorName)){
    			break;
    		}
    		index++;
    	}
    	if(index >= builderSelectors.size()){
    		throw new RuntimeException(
    				String.format("BuilderSelector named %s does not exist",builderSelectorName));
    	}
    	return index;
    }
    
    
    public TemplateBuilder select(Type target){
    	for(BuilderSelector selector : builderSelectors){
    		if(selector.matchType(target)){
    			return selector.getTemplateBuilder(target);
    		}
    	}
    	return null;
    }

	public TemplateBuilder getForceBuilder() {
		return forceBuilder;
	}


	public void setForceBuilder(TemplateBuilder forceBuilder) {
		this.forceBuilder = forceBuilder;
	}
	
	
	

}
