package spring_basic.part4_membership_Jdbc.repository;

import org.springframework.jdbc.datasource.DataSourceUtils;
import spring_basic.part4_membership_Jdbc.domain.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository{
    //변수
    //DataSource : ConnectionPool 관리를 위한 객체 ==> Connection 얻기/반납
    private final DataSource dataSource;

    //생성자
    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //메서드
    //DataSourceUtils : JNDI와 연결 얻기/반환 메서드 제공
    //JNDI : Java Naming and Directory Interface ==> 디렉토리 서비스에서 제공하는 데이터 및 객체를 발견/참고하기 위한 자바 API
    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    //DataSourceUtils를 통해 ConnectionPool과 연결 해제
    private void releaseConnecion(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    //Connection, PreparedStatement, ResultSet은 운영체제의 자원을 사용하기에 close를 하지 않으면 자원 고갈 위험이 존재
    private void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet)
    {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                releaseConnecion(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null; //query 결과 저장

        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);                      //DB 상에서 자동 생성된 key(id)를 가져오는 query
            preparedStatement.setString(1, member.getName()); //sql의 parameterIndex번째 ?에 member.getName()을 넣는다.
            preparedStatement.executeUpdate();                             //DB로 설정한 query 전달

            resultSet = preparedStatement.getGeneratedKeys();  //DB로 query를 전달한 결과를 받는다.

            //resultSet에 결과가 있다면 그 값을 받아오고, 없다면 Exception으로 조회 실패를 알린다.
            if (resultSet.next()) {
                //getLong(1) : 받아온 결과의 첫번째 열에 저장된 값을 Long으로 검색한다.
                member.setId(resultSet.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);   //sql의 1번째 ?에 Long 형식으로 id를 넣는다

            resultSet = preparedStatement.executeQuery();    //DB로 query 전달

            if(resultSet.next()) {
                Member member = new Member();

                member.setId(resultSet.getLong("id"));          //받아온 결과의 id label에 저장된 값을 Long으로 검색
                member.setName(resultSet.getString("name"));    //받아온 결과의 name label에 저장된 값을 String으로 검색

                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);

            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                Member member = new Member();

                member.setId(resultSet.getLong("id"));
                member.setName(resultSet.getString("name"));

                return Optional.of(member);
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from member";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            List<Member> members = new ArrayList<>(); //모든 결과를 저장할 리스트
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Member member = new Member();

                member.setId(resultSet.getLong("id"));
                member.setName(resultSet.getString("name"));
                members.add(member);
            }

            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }
}
