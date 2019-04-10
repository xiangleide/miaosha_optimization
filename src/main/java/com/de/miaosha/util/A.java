package com.de.miaosha.util;



public abstract class A {
	{
		System.out.println("ss");
	}
	
	double d = 5.3e12;
	
	public   abstract int test();
	
	public static void main(String[] args) {
		/*
		 * Byte a = 127; a++; System.out.println(a);
		 */
		d d=new D(1);
		System.out.println(func());
	}
	public static int func (){
	    try{
	        return 1;
	    }catch (Exception e){
	        return 2;
	    }finally{
	        return 3;
	    }
	}
}

class b{}
class c{}

class d extends b{
	int s;
	public d(int s) {
		this.s = s;
	}
	
}



class Demo {
	class Super {
		int flag = 1;

		Super() {
			test();
		}

		void test() {
			System.out.println("Super.test() flag=" + flag);
		}
	}

	class Sub extends Super {
		Sub(int i) {
			flag = i;
			System.out.println("Sub.Sub()flag=" + flag);
		}

		void test() {
			System.out.println("Sub.test()flag=" + flag);
		}
	}

	public static void main(String[] args) {
		new Demo().new Sub(5);
	}
}
