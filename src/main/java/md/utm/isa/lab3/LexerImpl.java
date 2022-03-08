package md.utm.isa.lab3;

import md.utm.isa.utils.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LexerImpl implements Lexer{
    private final List<Token> tokenList = new ArrayList<>();
    private int currentPos = 0;

    public LexerImpl(String pathName) throws Exception {
        String source = readSourceFile(pathName);
        analyze(source);
    }

    public String getPrintedResult(){
        return this.tokenList.toString();
    }

    @Override
    public List<Token> analyze(String source) {
        byte [] byteStream = source.getBytes(StandardCharsets.UTF_8);
        int streamLen = byteStream.length;

        while(currentPos<streamLen){
            checkString(byteStream, currentPos);
            checkNumber(byteStream, currentPos);
            checkIdentifier(byteStream, currentPos);
            checkToken(byteStream, currentPos);
            currentPos+=1;
        }

        return tokenList;
    }

    private String readSourceFile (String pathName) throws Exception {
        File file = FileUtil.getFileFromResources(pathName);
        Path filePath = file.toPath();

        return Files.readString(filePath);
    }

    private void checkToken(byte[] byteStream, int bytePos){
        if (bytePos>=byteStream.length){
            return;
        }

        Token token = new Token();
        String firstToken = String.valueOf((char)byteStream[bytePos]);


        // Check for the single token
        if (TokenMapper.isSingleToken(firstToken)){
            token = new Token(TokenMapper.getTokenType(firstToken),
                    firstToken, null, bytePos, bytePos + firstToken.length());
        }

        checkForComments(byteStream,  bytePos);

        if (bytePos+1<=byteStream.length-1){
            byte[] subarray = new byte[2];
            System.arraycopy(byteStream, bytePos, subarray, 0, 2);
            String doubleToken = new String(subarray);

            if (TokenMapper.isDoubleToken(doubleToken)){
                token = new Token(TokenMapper.getTokenType(doubleToken), doubleToken, null, bytePos,
                        bytePos+doubleToken.length());
                currentPos+=1;
            }
        }
        addToken(token);
    }

    private void checkForComments(byte[] stream, int bytePos){
        if (stream[bytePos] == '#'){
            while (bytePos<stream.length && stream[bytePos] != '\n'){
                bytePos++;
            }
            currentPos = bytePos;
        }
    }

    private void checkString(byte[] stream, int bytePos){
        if (stream[bytePos]== '"'){
            Token token = new Token();
            token.setStartPos(bytePos);
            bytePos++;
            while (bytePos<stream.length && stream[bytePos] != '"'){
                bytePos++;
            }

            bytePos++;
            token.setEndPos(bytePos);
            currentPos = bytePos;

            byte[] subarray = new byte[token.getEndPos() - token.getStartPos()];
            System.arraycopy(stream, token.getStartPos(), subarray, 0, subarray.length);

            token.setTokenType(TokenType.STRING_LITERAL);
            token.setLiteral(new String(subarray));
            addToken(token);
        }
    }

    private void checkIdentifier(byte[] stream, int bytePos){

        if ( isAlphaChar((char) stream[bytePos]) || isDigit((char) stream[bytePos])){
            Token token = new Token();
            token.setStartPos(bytePos);

            while (bytePos<stream.length && (isAlphaChar((char) stream[bytePos]) || isDigit((char) stream[bytePos]))){
                bytePos++;
            }

            token.setEndPos(bytePos);
            currentPos = bytePos;

            byte[] subarray = new byte[token.getEndPos() - token.getStartPos()];
            System.arraycopy(stream, token.getStartPos(), subarray, 0, subarray.length);

            String value = new String(subarray);

            if (TokenMapper.isToken(value)){
                token.setTokenType(TokenMapper.getTokenType(value));
                token.setLexeme(value);
                addToken(token);
                return;
            }

            token.setTokenType(TokenType.IDENTIFIER);
            token.setLiteral(value);
            addToken(token);
        }
    }


    private void checkNumber(byte[] stream, int bytePos){
        if (stream[bytePos]<='9' && stream[bytePos]>='0'){
            Token token = new Token();
            token.setStartPos(bytePos);
            while(bytePos<stream.length && stream[bytePos]<='9' && stream[bytePos]>='0'){
                bytePos++;
            }

            if (bytePos<stream.length && isAlphaChar((char)stream[bytePos])){
                return;
            }
            token.setEndPos(bytePos);
            currentPos = bytePos;

            byte[] subarray = new byte[token.getEndPos() - token.getStartPos()];
            System.arraycopy(stream, token.getStartPos(), subarray, 0, subarray.length);

            token.setTokenType(TokenType.NUMBER_LITERAL);
            token.setLiteral(Double.parseDouble(new String(subarray)));
            addToken(token);
        }
    }

    private void addToken (Token token) {
        if (token.getTokenType()!=null)
            this.tokenList.add(token);
    }

    private boolean isAlphaChar(char ch){
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch == '_');
    }

    private boolean isDigit(char ch) {
        return (ch>='0' && ch<='9');
    }
}
