package jp.co.metateam.library.model;

import java.security.Timestamp;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 書籍マスタDTO
 */
@Getter
@Setter
public class BookMstDto {

    
    private Long id; 
    
   
    private String isbn;

    boolean errEmailFlg = false;


   
    @Size(max = 255)
    private String title;
    
    private Timestamp deletedAt;

    private BookMst bookMst;
}
