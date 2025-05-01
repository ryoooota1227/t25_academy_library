package jp.co.metateam.library.controller;
 
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;
 
/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {
   
    private final BookMstService bookMstService;
 
    @Autowired
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService;
    }
 
    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
       
        model.addAttribute("bookMstList", bookMstList);
 
        return "book/index";
    }
 
    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }
 
        return "book/add";//書籍登録画面
    }
    //新しく入力
    @PostMapping("/book/add")
    public String register(@Valid @ModelAttribute BookMstDto bookMstDto, BindingResult result, RedirectAttributes ra){

        try{      
            boolean errIsbnFlg = false;
            boolean errEmpbookFlg = false;
            String isbn = bookMstDto.getIsbn();
            String title = bookMstDto.getTitle();

            BookMst isbnExist = this.bookMstService.selectByIsbn(bookMstDto.getIsbn());

             List<String> errorMessages = new ArrayList<>();  // エラーメッセージのリスト
 
        //書籍名が未入力→”書籍名は必須です”
        if (bookMstDto == null || bookMstDto.getTitle().trim().equals("")){
            //どっちかがtrueなら、この中の処理が実行される
            errorMessages.add("書籍名は必須です");
            result.rejectValue("title", "error.required", "書籍名は必須です");
            errEmpbookFlg = true;
   
}

        if(isbn == null || title.isEmpty()){
            result.rejectValue("isbn", "error.value", "ISBNは必須です");
            errIsbnFlg = true;
        }

        if(title == null || title.isEmpty()){
            result.rejectValue("title", "error.value", "書籍名は必須です");
            errEmpbookFlg = true;
        }

        if(isbn.length() != 13 ){
            result.rejectValue("isbn", "error.value", "ISBNは13文字で入力してください");
            errEmpbookFlg = true;
        }

        if(isbn.matches("/^[0-9]+$/")){
            result.rejectValue("isbn", "error.value", "ISBNは半角で入力してください");
            errEmpbookFlg = true;
        }

        if(title.length() > 255 ){
            result.rejectValue("title", "error.value", "書籍名は255文字以下で入力してください");
            errEmpbookFlg = true;
        }

        //重複チェック↓
        // もしISBNがすでに存在している場合、エラーを返す
      
        if ( isbnExist != null) {
        errorMessages.add("登録済みのISBNです");
        result.rejectValue("isbn", "error.exists", "登録済みのISBNです");
        errIsbnFlg = true;//}
        }
        
        // エラーがあれば、エラーメッセージリストをフラッシュ属性に渡す
        if (errEmpbookFlg || errIsbnFlg) {
            ra.addFlashAttribute("errorMessages", errorMessages);
            return "book/add"; // エラーがある場合、フォーム画面にリダイレクト
        }

        if (errIsbnFlg || errEmpbookFlg){
            throw new Exception("Account already exists.");
        }
        
            bookMstService.save(bookMstDto);

            return "redirect:/book/index";

        } catch (Exception e) {
            log.error(e.getMessage());
 
            ra.addFlashAttribute("bookMstDto", bookMstDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.bookMstDto", result);
 
            return "redirect:/book/add";
        }
    }
}