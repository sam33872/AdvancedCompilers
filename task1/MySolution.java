/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author 184504
 */
class MySolution implements Codegen{
    private String c;
    private int count;
    
    public MySolution(){
        count = 0;
        c = "";
    }
    
    @Override
    public String codegen ( Program p ) throws CodegenException {
        c = "";
        c = c + "   .align 2";
        newLine();
        c = c + "   .globl entry_point";
        newLine();
        c = c + "   .text";
        newLine();
        c = c + "entry_point:";
        newLine();
        c = c + "   jal " + p.decls.get(0).id + "_entry";
        newLine();
        
        for(int i = 0; i < p.decls.size(); i++){
            
            genDec(p.decls.get(i));
            if(i == 0){
                c = c + "   li a7 10";
                newLine();
                c = c + "   ecall";
                newLine();
            }
            else{
                c = c + "   jr ra";
                newLine();
            }
        }
        return c;
    
    }
    
    public void genDec(Declaration d){
            String label = d.id + "_entry:";
        
            c = c + label;
            newLine();
            //c = c + "   sw s0 0(sp)";
            //newLine();
            //c = c + "   addi sp sp -4";
            //newLine();
            
            int sizeAR = ((2 + d.numOfArgs) * 4);
            
            c = c + "   mv s0 sp";
            newLine();
            c = c + "   sw ra 0(sp)";
            newLine();
            c = c + "   addi sp sp -4";
            newLine();
            
            genExp(d.body);
            
            c = c + "   lw ra 4(sp)";
            newLine();
            c = c + "   addi sp sp " + sizeAR;
            newLine();
            c = c + "   lw s0 0(sp)";
            newLine();
            
    }
    
    public void genExp (Exp e){
        if(e instanceof IntLiteral){
            c = c + "   li a0 " + ((IntLiteral) e).n;
            newLine();
        }
        else if(e instanceof Variable){
            int offset = 4 * ((Variable) e).x;
            c = c + "lw a0 " + offset + "(s0)";
            newLine();
        }
        else if(e instanceof If){
            String elseBranch = generateLabel("else"); 
            String thenBranch = generateLabel("then");
            String exitLabel = generateLabel("exit");
            
            genExp(((If) e).l);
            
            c = c + "sw a0 0(sp)";
            newLine();
            c = c + "addi sp sp -4";
            newLine();
            
            genExp(((If) e).r);
            
            c = c + "lw t1 4(sp)";
            newLine();
            c = c + "addi sp sp 4";
            newLine();
            // More complex for task 2/3
            c = c + "beq a0 t1 " + thenBranch;
            newLine();
            
            c = c + elseBranch + ":";
            newLine();
            
            genExp(((If) e).elseBody);
            
            c = c + "b " + exitLabel;
            newLine();
            
            c = c + thenBranch + ":";
            newLine();
            
            genExp(((If) e).thenBody);
            
            c = c + exitLabel + ":";
            newLine();   
        }
        else if(e instanceof Binexp){
            genExp(((Binexp) e).l);
            
            c = c + "sw a0 (sp)";
            newLine();
            c = c + "addi sp sp -4";
            newLine();
            
            genExp(((Binexp) e).r);
            
            c = c + "lw t1 4(sp)";
            newLine();
            
            if(((Binexp) e).binop instanceof Plus){
                c = c + "add a0 t1 a0";
                newLine();
            }
            else if(((Binexp) e).binop instanceof Minus){
                c = c + "sub a0 t1 a0";
                newLine();
            }
            
            c = c + "addi sp sp 4";
            newLine();
        }
        else if(e instanceof Invoke){
            int size = ((Invoke) e).args.size();
            c = c + "   sw s0 0(sp)";
            newLine();
            c = c + "   addi sp sp -4";
            newLine();
            for(int j = size; j > 0; j--){
                genExp(((Invoke) e).args.get(j-1));
                c = c + "   sw a0 0(sp)";
                newLine();
                c = c + "   addi sp sp -4";
                newLine();
            }   
            c = c + "   jal " + ((Invoke) e).name + "_entry";
            newLine();
        }
        else if(e instanceof Exp){
            genExp(e);
        }
    }
    
    public void newLine(){
        c = c + "\n";
    }
    
    public String generateLabel(String l){
        String label = l + count;
        count++;
        return label;
    }
}
