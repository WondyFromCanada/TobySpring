package toby.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
		//계산 결과를 저장할 변수의 초기값
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
			String line = null;
			
			while((line = br.readLine()) != null) {
				//res : 콜백이 계산한 값을 저장해뒀다가 다음 라인 계산에 다시 사용한다.
				//line : 각 라인의 내용을 가지고 계산하는 작업만 콜백에게 맡긴다.
				res = callback.doSomethingWithLine(line, res);
			}
			
			return res;
		} catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch(IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filepath));
			//콜백 오브젝트 호출
			//템플릿에서 만든 컨텍스트 정보인 BufferedReader를 전달해주고 콜백의 작업 결과를 받아둔다.
			int ret = callback.doSomethingWithReader(br);
			return ret;
		} catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch(IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
				
	}
	
	public Integer calcSum(String filepath) throws IOException {
		LineCallback sumCallback = new LineCallback<Integer>() {
			public Integer doSomethingWithLine(String line, Integer value) {
				return value + Integer.valueOf(line);
			}
		};
		
		return lineReadTemplate(filepath, sumCallback, 0);
		
		/*BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer sum = 0;
				String line = null;
				
				while((line = br.readLine()) != null) {
					sum += Integer.valueOf(line);
				}
				
				return sum;
			}
		};
		
		return fileReadTemplate(filepath, sumCallback);*/
		
		/*//한 줄씩 읽기 편하도록 BufferedReader로 파일을 가져온다.
		BufferedReader br = null; 
		
		try {
			br = new BufferedReader(new FileReader(filepath));
			Integer sum = 0;
			String line = null;
			
			//마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
			while((line = br.readLine()) != null) {
				sum += Integer.valueOf(line);
			}
			
			return sum;
			
		} catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			//BufferedReader 오브젝트가 생성되기 전에 예외가 발생할 수도 있으므로 반드시 null 체크를 먼저 해야 한다.
			if(br != null) {
				try {
					//한 번 연 파일은 반드시 닫아준다.
					br.close();
				} catch(IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}*/
	}

	public Integer calcMultiply(String filepath) throws IOException {
		LineCallback sumCallback = new LineCallback<Integer>() {
			public Integer doSomethingWithLine(String line, Integer value) {
				return value * Integer.valueOf(line);
			}
		};
		
		return lineReadTemplate(filepath, sumCallback, 1);
		
		/*BufferedReaderCallback multiplyCallback = new BufferedReaderCallback() {
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer multiply = 1;
				String line = null;
				
				while((line = br.readLine()) != null) {
					multiply *= Integer.valueOf(line);
				}
				
				return multiply;
			}
		};
		
		return fileReadTemplate(filepath, multiplyCallback);*/
	}
	
	public String concatenate(String filepath) throws IOException {
		LineCallback<String> concatenateCallback = new LineCallback<String>() {
			public String doSomethingWithLine(String line, String value) {
				return value + line;
			}
		};
		//템플릿 메소드의 T는 모두 스트링이 된다.
		return lineReadTemplate(filepath, concatenateCallback, "");
	}
}
