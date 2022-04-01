// PARSER
parser grammar lab3Parser;



// Function declarations

declaration:  varDecl;


functionDecl: FUNC IDENTIFIER (signature block?);

varDecl: VAR (varSpec | L_PAREN (varSpec eos)* R_PAREN);

block: L_CURLY statementList? R_CURLY;

statementList: (eos? statement eos)+;

statement:
	declaration
	| labeledStmt
	| simpleStmt
	| goStmt
	| returnStmt
	| breakStmt
	| continueStmt
	| gotoStmt
	| fallthroughStmt
	| block
	| ifStmt
	| switchStmt
	| selectStmt
	| forStmt
	| deferStmt;

simpleStmt:
     incStmt
	| assignment
	| exprStmt
	| shortVarDecl;

exprStmt: expression;

incStmt: expression (INC_PLUS | INC_MINUS);

assignment: expressionList assign_op expressionList;

assign_op: (PLUS | MINUS | OR | MULT | DIV)? EQUAL;
