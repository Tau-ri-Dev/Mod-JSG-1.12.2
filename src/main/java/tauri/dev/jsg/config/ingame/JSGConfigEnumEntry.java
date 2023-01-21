package tauri.dev.jsg.config.ingame;

public class JSGConfigEnumEntry {
    public String name;
    public String value;

    public JSGConfigEnumEntry(String name, String value){
        this.name= name;
        this.value= value;
    }

    public int getIntValue(){
        try{
            return Integer.parseInt(this.value);
        }
        catch(Exception ignored){}
        return -1;
    }

    @Override
    public String toString(){
        return "[name=" + name + ", value=" + value + "]";
    }
}
