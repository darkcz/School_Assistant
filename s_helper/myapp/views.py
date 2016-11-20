#coding=utf-8
from django.shortcuts import render,render_to_response
from django import forms
from .models import User
from django.http import HttpResponse
from django.contrib import auth
from django.template import RequestContext


# 接收POST请求数据
class userFormlogin(forms.Form):
      username = forms.CharField(label='用户名',max_length=100)
      password = forms.CharField(label='密码',widget=forms.PasswordInput())



def login (request):                          #登录
      if request.method == "POST":
            uform = userFormlogin(request.POST)
            if uform.is_valid():
                  username = uform.cleaned_data['username']  #get 表单
                  password = uform.cleaned_data['password']

                  userResult = User.objects.filter(username=username,password=password)

                  if (len(userResult)>0):  #表示有此人

                        return render_to_response('myapp/success.html' , {'operation':"登录"})  //弹出登录成功页面

                  else:
                        return HttpResponse("该用户不存在")                 //登录失败
      else:
            uform = userFormlogin()

      return render(request,"myapp/userlogin.html",{'uform':uform})       


class userRegister(forms.Form):
      username = forms.CharField(label='用户名',max_length=100)
      password1 = forms.CharField(label='密码',widget=forms.PasswordInput())
      password2 = forms.CharField(label='确认密码',widget=forms.PasswordInput())
      email = forms.EmailField(label='电子邮件')
                     
def register(request):
      if (request.method !="POST"):
            uform =userRegister()
            
      else:
            uform =userRegister(request.POST)
            if uform.is_valid():
                  username =  uform.cleaned_data['username']
                  errors = []  

                  userResult =User.objects.filter(username = username)

                  if (len(userResult) ==1 ) :         #用户存在
                       errors.append("用户名已经存在")
                       return render(request,"myapp/userRegister.html",{"errors":errors})
                  else:
                        password1 = uform.cleaned_data['password1']
                        password2 = uform.cleaned_data['password2']            //得到表单

                        if (password1 !=password2):
                              errors.append("密码不一样请重新输入")
                              uform = userFormlogin()
                              return render(request,"myapp/userRegister.html",{"errors":errors }) 
                        else:                              
                              email = uform.cleaned_data['email']
                              password = password1

                              user = User.objects.create(username=username,password=password,email=email)       '''写入数据库'''
                              user.save()
                              uform = userFormlogin()
                              return render_to_response("myapp/userRegister.html",{"errors":"注册成功"})


      return render_to_response("myapp/userRegister.html",{"uform":uform})
























                              
      

