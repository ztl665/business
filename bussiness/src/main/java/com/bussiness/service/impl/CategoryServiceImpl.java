package com.bussiness.service.impl;

import com.bussiness.common.ResponseCode;
import com.bussiness.common.ServerResponse;
import com.bussiness.dao.CategoryMapper;
import com.bussiness.pojo.Category;
import com.bussiness.service.ICategoryService;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

   /*添加商品类别*/
    @Override
    public ServerResponse addCategory(Category category) {

        if(category==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数不能为空");
        }
        int result=categoryMapper.insert(category);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"添加品类失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }
    /*修改更新类别*/
    @Override
    public ServerResponse updateCategory(Category category) {
        if(category==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数不能为空");
        }
        if(category.getId()==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"类别id必须传");
        }
        int result= categoryMapper.updateByPrimaryKey(category);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"更新品类失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }
    /*查看平级类别*/
    @Override
    public ServerResponse getCategoryById(Integer categoryId) {
        if(categoryId==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"id必传");
        }
        List<Category> categoryList=categoryMapper.selectCategoryById(categoryId);
        return ServerResponse.serverResponseBySuccess(categoryList,"成功");
    }
    /*递归获取*/
    @Override
    public ServerResponse deepCategory(Integer categoryId) {
        if(categoryId==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"id必传");
        }
        Set<Category> categorySet= Sets.newHashSet();
        Set<Category> categorySet1=findAllChildCategory(categorySet,categoryId);
        Set<Integer> categoryIds=Sets.newHashSet();
        Iterator<Category> iterator=categorySet1.iterator();
        while(iterator.hasNext()){
            Category category=iterator.next();
            categoryIds.add(category.getId());
        }
        return ServerResponse.serverResponseBySuccess(categoryIds);
    }

    @Override
    public ServerResponse<Category> selectCategory(Integer categoryId) {
        if(categoryId==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"id必传");
        }
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        return ServerResponse.serverResponseBySuccess(category);
    }

    public Set<Category> findAllChildCategory(Set<Category> categorySet,Integer categoryId){
        //查看categoryId的类别信息
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查看categoryId的平级子类
        List<Category> categoryList=categoryMapper.selectCategoryById(categoryId);

        if(categoryList!=null&&categoryList.size()>0){
            for(Category category1:categoryList){//遍历categoryList
                findAllChildCategory(categorySet,category1.getId());
            }
        }
        return categorySet;
    }
}
