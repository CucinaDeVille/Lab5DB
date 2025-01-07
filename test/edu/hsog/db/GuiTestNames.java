package edu.hsog.db;

import org.assertj.swing.fixture.*;

public class GuiTestNames {
    
    private static final String CONNECTION_BUTTON = "connectionJBtn";
    private static final String EXIT_BUTTON = "exitJBtn";
    private static final String COUNT_BUTTON = "countJBtn";
    private static final String SAVE_COMMENT_BUTTON = "saveCommentJBtn";
    private static final String DELETE_BUTTON = "deleteJBtn";
    private static final String FIRST_BUTTON = "firstJBtn";
    private static final String LAST_BUTTON = "lastJBtn";
    private static final String LOADIMAGE_BUTTON = "loadImageJBtn";
    private static final String LOGIN_BUTTON = "loginJBtn";
    private static final String NEXT_BUTTON = "nextJBtn";
    private static final String PREVIOUS_BUTTON = "previousJBtn"; 
    private static final String REGISTER_BUTTON = "registerJBtn";
    private static final String SAVE_BUTTON = "saveJBtn";
    private static final String SEARCH_BUTTON = "searchJBtn";
    private static final String CLEAR_BUTTON = "clearJBtn";
    
    private static final String COMMENT_TEXTFIELD = "commentJTxt";
    private static final String KEYWORD_TEXTFIELD = "keywordJTxt";
    private static final String GEDGET_TEXTFIELD = "gadgetUrlJTxt";
    private static final String PASSWORD_TEXTFIELD = "passwdJTxt";
    private static final String USER_TEXTFIELD = "userJTxt";

            
    private static final String COUNT_LABEL = "countJLbl";
    private static final String IMAGE_LABEL = "imageJLbl";
    private static final String STATUS_LABEL = "statusJLbl";
    private static final String RATING_LABEL = "ratingJLbl";
    private static final String OWNER_LABEL = "ownerJLbl";
    
    private static final String LIKE_SLIDER = "likeJSli";
    
    private static final String COMMENTS_TEXTAREA = "commentsJTxtA";
    private static final String DESCRIPTION_TEXTAREA = "descriptJTxtA";

    private static final String IMAGE_FILE_CHOOSER = "imageJFch";

    // Generate rest with LLM
    // Input:
    // private static final String CONNECTION_BUTTON = "connectionJBtn";
    // private static final String EXIT_BUTTON = "exitJBtn";
    // private static final String COMMENTS_TEXTAREA = "commentsJTxtA";
    // Output:
    // public static JButtonFixture getCONNECTION_BUTTON(FrameFixture frame) {
    //        return frame.button(CONNECTION_BUTTON);
    //    }
    // public static JButtonFixture getEXIT_BUTTON(FrameFixture frame) {
    //        return frame.button(EXIT_BUTTON);
    //    }
    // public static JTextComponentFixture getCOMMENTS_TEXTAREA(FrameFixture frame) {
    //    return frame.textBox(COMMENTS_TEXTAREA);
    // }
    //
    // Generate output for the following input:
    // ...
    // ... <copy> and <paste>
    // ...


    public static JButtonFixture getCONNECTION_BUTTON(FrameFixture frame) {
        return frame.button(CONNECTION_BUTTON);
    }

    public static JButtonFixture getEXIT_BUTTON(FrameFixture frame) {
        return frame.button(EXIT_BUTTON);
    }

    public static JButtonFixture getCOUNT_BUTTON(FrameFixture frame) {
        return frame.button(COUNT_BUTTON);
    }

    public static JButtonFixture getSAVE_COMMENT_BUTTON(FrameFixture frame) {
        return frame.button(SAVE_COMMENT_BUTTON);
    }

    public static JButtonFixture getDELETE_BUTTON(FrameFixture frame) {
        return frame.button(DELETE_BUTTON);
    }

    public static JButtonFixture getFIRST_BUTTON(FrameFixture frame) {
        return frame.button(FIRST_BUTTON);
    }

    public static JButtonFixture getLAST_BUTTON(FrameFixture frame) {
        return frame.button(LAST_BUTTON);
    }

    public static JButtonFixture getLOADIMAGE_BUTTON(FrameFixture frame) {
        return frame.button(LOADIMAGE_BUTTON);
    }

    public static JButtonFixture getLOGIN_BUTTON(FrameFixture frame) {
        return frame.button(LOGIN_BUTTON);
    }

    public static JButtonFixture getNEXT_BUTTON(FrameFixture frame) {
        return frame.button(NEXT_BUTTON);
    }

    public static JButtonFixture getPREVIOUS_BUTTON(FrameFixture frame) {
        return frame.button(PREVIOUS_BUTTON);
    }

    public static JButtonFixture getREGISTER_BUTTON(FrameFixture frame) {
        return frame.button(REGISTER_BUTTON);
    }

    public static JButtonFixture getSAVE_BUTTON(FrameFixture frame) {
        return frame.button(SAVE_BUTTON);
    }

    public static JButtonFixture getSEARCH_BUTTON(FrameFixture frame) {
        return frame.button(SEARCH_BUTTON);
    }

    public static JButtonFixture getCLEAR_BUTTON(FrameFixture frame) {
        return frame.button(CLEAR_BUTTON);
    }

    public static JTextComponentFixture getCOMMENT_TEXTFIELD(FrameFixture frame) {
        return frame.textBox(COMMENT_TEXTFIELD);
    }

    public static JTextComponentFixture getKEYWORD_TEXTFIELD(FrameFixture frame) {
        return frame.textBox(KEYWORD_TEXTFIELD);
    }

    public static JTextComponentFixture getGADGET_TEXTFIELD(FrameFixture frame) {
        return frame.textBox(GEDGET_TEXTFIELD);
    }

    public static JTextComponentFixture getPASSWORD_TEXTFIELD(FrameFixture frame) {
        return frame.textBox(PASSWORD_TEXTFIELD);
    }

    public static JTextComponentFixture getUSER_TEXTFIELD(FrameFixture frame) {
        return frame.textBox(USER_TEXTFIELD);
    }

    public static JLabelFixture getCOUNT_LABEL(FrameFixture frame) {
        return frame.label(COUNT_LABEL);
    }

    public static JLabelFixture getIMAGE_LABEL(FrameFixture frame) {
        return frame.label(IMAGE_LABEL);
    }

    public static JLabelFixture getSTATUS_LABEL(FrameFixture frame) {
        return frame.label(STATUS_LABEL);
    }

    public static JLabelFixture getRATING_LABEL(FrameFixture frame) {
        return frame.label(RATING_LABEL);
    }

    public static JLabelFixture getOWNER_LABEL(FrameFixture frame) {
        return frame.label(OWNER_LABEL);
    }

    public static JSliderFixture getLIKE_SLIDER(FrameFixture frame) {
        return frame.slider(LIKE_SLIDER);
    }

    public static JTextComponentFixture getCOMMENTS_TEXTAREA(FrameFixture frame) {
        return frame.textBox(COMMENTS_TEXTAREA);
    }

    public static JTextComponentFixture getDESCRIPTION_TEXTAREA(FrameFixture frame) {
        return frame.textBox(DESCRIPTION_TEXTAREA);
    }

    public static JFileChooserFixture getIMAGE_FILE_CHOOSER(FrameFixture frame) {
        return frame.fileChooser(IMAGE_FILE_CHOOSER);
    }
}
