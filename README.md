# 만든이유
디스코드 봇을 한번 만들어 보고 싶었고, 당시에 롤을 많이 했는데, 항상 친구들과 디스코드를 하면서 했다. <br>
그래서 굳이 웹사이트 op.gg를 들어가지 않고 바로 팀원의 최근전적을 보기위해 만들었다. <br>
게임을 캐리하고나서 자랑하는 용도로도 나쁘지 않다.

---
# 실행 화면
![image](https://user-images.githubusercontent.com/64322765/201470093-81a20107-8fe0-4e4e-a398-ca0dfc5e0179.png)
 
---
# 수정해야 할 부분

1. 최근 46경기 기록 > 최근 10경기 기록 바꿔야함. String(win+lose)를 integer(win)+integer(lose)로

2. 최근 전적에서 킬/데스/어시스트에서 데스가 0인 경우 평점을 Perfect로 예외 처리해야 함.

3. UserCommand.class 에서 종종 null을 리턴한다. 빈 배열 또는 Optional로 감싸주도록 변경 해야함


---
# 1년 후 시점인 2023년 7월 28일에 깃허브 코드 보는 소감
1. Java stream을 이용하자.
2. 예외 처리를 많이 신경 쓰자. 특히 Null 관련 예외처리
3. Java Optional을 이용하자.
4. 클래스 멤버 변수가 많다면 Builder 패턴을 이용하자.
   
