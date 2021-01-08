# How to Create a Certificate in Spring Boot


### 1. pom.xml에 bouncycastle dependency 추가 
 > EC 알고리즘을 활용하여 키쌍을 생성 후 인증서를 만들기 위함임! 

```
 <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcpkix-fips</artifactId>
            <version>1.0.3</version>
 </dependency>

```
다른 버전의 의존성을 주입하고 싶다면 메이븐 레포지토리에서 찾아서 pom.xml에 추가 !! 


### 2. spring boot의 main 메서드에 아래 코드 추가 ! 
> 굳이 메인 메서드에 선언 할 필요는 없음 사용하기 전에만 provider를 추가해주면 된다! 

``` java 

@EnableAsync
@SpringBootApplication
public class OmpassStressTesterApplication {

    public static void main(String[] args)  {
        SpringApplication.run(OmpassStressTesterApplication.class, args);
        Security.addProvider(new BouncyCastleFipsProvider()); // 이 코드를 꼭 추가해줘야함 

    } 
}

```

### 3. EC.java 코드를 참고한다. 

> MVC 패턴으로 작성되지 않은 이유는 회사에서 개발한 내용이기에 전체 코드를 공개 할 순 없어서이다 ! EC.java 를 참고하여 
키쌍을 만들고 해당 키쌍을 통해 인증서를 만들어 준다 !!! 자세한 설명은 코드와 주석 참고!

### 4. 사용 방법 
 ``` java 
 // 이 코드는 EC 인스턴스를 생성하기 전에 무조건 선언 해야함
 Security.addProvider(new BouncyCastleFipsProvider()); 
 
 EC ec = new EC();
 Keypair keypair = ec.generateKeyPair();

 X509Certificate cert = ec.generateCert(keypair,30,algorithm, dn,provider);

 // algorithm, dn,provider 이 부분은 필요에 따라 알아서 넣음 된다! 

```


추후에 EC 알고리즘을 통해 암호화 및 복호화 하는 내용 추가 할 것 ! 
