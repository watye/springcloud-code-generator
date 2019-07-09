package ${package}.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ${package}.web.AbstractTest;
import ${package}.dao.entity.${className};
import ${package}.web.service.${className}WebService;

public class ${className}ServiceTest extends AbstractTest{
	@Autowired
	private ${className}WebService ${classname}WebService;
	
	@Test
	public void testSave(){
		${className} entity = new ${className}();
		${classname}WebService.save(entity);
	}
	
	@Test
	public void testGetById(){
		${classname}WebService.getById(1L);
	}
	
	@Test
	public void testGetByIds(){
		List<Long> ids = new ArrayList<>();
		ids.add(1L);
		ids.add(2L);
		${classname}WebService.getByIds(ids);
	}
	
	@Test
	public void testGet(){
		${className} entity = new ${className}();
		${classname}WebService.get(entity);
	}
	
	@Test
	public void testFindList(){
		${className} entity = new ${className}();
		${classname}WebService.findList(entity);
	}
	
	@Test
	public void testFindAll(){
		${classname}WebService.findAll();
	}
	
	@Test
	public void testGetCount(){
		${className} entity = new ${className}();
		${classname}WebService.getCount(entity);
	}
	
	@Test
	public void testUpdateById(){
		${className} entity = new ${className}();
		${classname}WebService.updateById(entity);
	}
	
	@Test
	public void testUpdateByIds(){
		${className} entity = new ${className}();
		${classname}WebService.updateByIds(entity,new Long[]{2L,3L});
	}
	
	@Test
	public void testDeleteById(){
		${classname}WebService.deleteById(4L);
	}
	
	@Test
	public void testDeleteByIds(){
		${classname}WebService.deleteByIds(Arrays.asList(2L,3L));
	}
	
	@Test
	public void testDelete(){
		${className} entity = new ${className}();
		${classname}WebService.delete(entity);
	}
}
