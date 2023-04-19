package shop.donutmarket.donut.domain.wishlist;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.wishlist.model.Wishlist;
import shop.donutmarket.donut.domain.wishlist.repository.WishlistRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WishlistRepositoryTest {
    
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private TestEntityManager tem; // 테스트하기 위한 EntityManager

    @BeforeEach
    void setUp(){
        autoincrementReset(); // autoincrement 보장해주는 메서드
        dataSetting(); // 초기 dummy 데이터 세팅
        tem.clear(); // 영속성 컨텍스트 비우기
    }

    @Test
    @DisplayName("Wishlist 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        
        // when
        Optional<Wishlist> wishlist = wishlistRepository.findById(id);

        // then
        wishlist.ifPresent(wishlist1 -> {
            assertNotNull(wishlist1);
            assertEquals(wishlist1.getId(), 1L);
        });
    }
    
    @Test
    @DisplayName("Wishlist 생성 테스트")
    void save_Test() {
        // given
        User user = User.builder().build();
        Board board = Board.builder().build();
        Wishlist wishlist = Wishlist.builder().user(user).board(board).build();

        // when
         wishlistRepository.save(wishlist);
        
        // then
        assertNotNull(wishlistRepository.findById(2L));
    }

    @Test
    @DisplayName("Wishlist 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        Wishlist wishlist = tem.find(Wishlist.class, id);

        // when
        if (wishlist != null) {
            tem.remove(wishlist);
            tem.flush();
        }

        // then
        assertNull(tem.find(Wishlist.class, id));
    }

    private void dataSetting() {
        User user = User.builder().build();
        Board board = Board.builder().build();
        Wishlist wishlist = Wishlist.builder().user(user).board(board).build();
        wishlistRepository.save(wishlist);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE wishlist ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
