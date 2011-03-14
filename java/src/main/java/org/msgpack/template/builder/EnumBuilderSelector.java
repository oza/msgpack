package org.msgpack.template.builder;

import java.lang.reflect.Type;

public class EnumBuilderSelector implements BuilderSelector {

	public static final String NAME = "EnumTemplateBuilder";
	
	public String getName(){
		return NAME;
	}
	
	@Override
	public boolean matchType(Type targetType) {
		return ((Class<?>)targetType).isEnum();
	}


	OriginalEnumTemplateBuilder builder = new OriginalEnumTemplateBuilder();

	@Override
	public TemplateBuilder getTemplateBuilder(Type targetType) {
		return builder;
	}

}
