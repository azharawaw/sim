-injars ant_output\jars\travoltage.jar
-injars lib\jcommon-1.0.5.jar
-outjars ant_output\jars\travoltage-pro.jar

-libraryjars 'C:\j2sdk1.4.2_05\jre\lib\rt.jar'

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontobfuscate
-verbose

#See: http://proguard.sourceforge.net/manual/examples.html#componentui
#-keep class * extends javax.swing.plaf.ComponentUI {
#    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
#}
#-keep class smooth.**{}
#-keep class smoothmetal.**{}

# Keep - Applications. Keep all application classes that have a main method.
-keepclasseswithmembers public class org.jfree.ui.about.resources.AboutResources{
}

# Keep - Applications. Keep all application classes that have a main method.
-keepclasseswithmembers public class edu.colorado.phet.travoltage.TravoltageApplication{
    public static void main(java.lang.String[]);
}

# Also keep - Enumerations. Keep a method that is required in enumeration
# classes.
-keepclassmembers class * extends java.lang.Enum {
    public **[] values();
}

# Also keep - Bean classes. Keep all bean classes along with their getters
# and setters.
-keep class edu.colorado.phet.qm.persistence.* {
    void set*(%);
    void set*(**);
    void set*(%[]);
    void set*(**[]);
    void set*(int,%);
    void set*(int,**);
    % get*();
    ** get*();
    %[] get*();
    **[] get*();
    % get*(int);
    ** get*(int);
}

#JFreeChartResources: For loading property files.
-keep class org.jfree.chart.resources.JFreeChartResources

#JCommonResources: property file loader for jcommon
-keep class org.jfree.resources.JCommonResources

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembernames class * {
    native <methods>;
}

# Remove - Math method calls. Remove all invocations of Math
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.Math {
    public static double sin(double);
    public static double cos(double);
    public static double tan(double);
    public static double asin(double);
    public static double acos(double);
    public static double atan(double);
    public static double toRadians(double);
    public static double toDegrees(double);
    public static double exp(double);
    public static double log(double);
    public static double log10(double);
    public static double sqrt(double);
    public static double cbrt(double);
    public static double IEEEremainder(double,double);
    public static double ceil(double);
    public static double floor(double);
    public static double rint(double);
    public static double atan2(double,double);
    public static double pow(double,double);
    public static int round(float);
    public static long round(double);
    public static double random();
    public static int abs(int);
    public static long abs(long);
    public static float abs(float);
    public static double abs(double);
    public static int max(int,int);
    public static long max(long,long);
    public static float max(float,float);
    public static double max(double,double);
    public static int min(int,int);
    public static long min(long,long);
    public static float min(float,float);
    public static double min(double,double);
    public static double ulp(double);
    public static float ulp(float);
    public static double signum(double);
    public static float signum(float);
    public static double sinh(double);
    public static double cosh(double);
    public static double tanh(double);
    public static double hypot(double,double);
    public static double expm1(double);
    public static double log1p(double);
}

# Remove - String method calls. Remove all invocations of String
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.String {
    public <init>();
    public <init>(byte[]);
    public <init>(byte[],int);
    public <init>(byte[],int,int);
    public <init>(byte[],int,int,int);
    public <init>(byte[],int,int,java.lang.String);
    public <init>(byte[],java.lang.String);
    public <init>(char[]);
    public <init>(char[],int,int);
    public <init>(java.lang.String);
    public <init>(java.lang.StringBuffer);
    public static java.lang.String copyValueOf(char[]);
    public static java.lang.String copyValueOf(char[],int,int);
    public static java.lang.String valueOf(boolean);
    public static java.lang.String valueOf(char);
    public static java.lang.String valueOf(char[]);
    public static java.lang.String valueOf(char[],int,int);
    public static java.lang.String valueOf(double);
    public static java.lang.String valueOf(float);
    public static java.lang.String valueOf(int);
    public static java.lang.String valueOf(java.lang.Object);
    public static java.lang.String valueOf(long);
    public boolean contentEquals(java.lang.StringBuffer);
    public boolean endsWith(java.lang.String);
    public boolean equalsIgnoreCase(java.lang.String);
    public boolean equals(java.lang.Object);
    public boolean matches(java.lang.String);
    public boolean regionMatches(boolean,int,java.lang.String,int,int);
    public boolean regionMatches(int,java.lang.String,int,int);
    public boolean startsWith(java.lang.String);
    public boolean startsWith(java.lang.String,int);
    public byte[] getBytes();
    public byte[] getBytes(java.lang.String);
    public char charAt(int);
    public char[] toCharArray();
    public int compareToIgnoreCase(java.lang.String);
    public int compareTo(java.lang.Object);
    public int compareTo(java.lang.String);
    public int hashCode();
    public int indexOf(int);
    public int indexOf(int,int);
    public int indexOf(java.lang.String);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(int);
    public int lastIndexOf(int,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.CharSequence subSequence(int,int);
    public java.lang.String concat(java.lang.String);
    public java.lang.String replaceAll(java.lang.String,java.lang.String);
    public java.lang.String replace(char,char);
    public java.lang.String replaceFirst(java.lang.String,java.lang.String);
    public java.lang.String[] split(java.lang.String);
    public java.lang.String[] split(java.lang.String,int);
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
    public java.lang.String toLowerCase();
    public java.lang.String toLowerCase(java.util.Locale);
    public java.lang.String toString();
    public java.lang.String toUpperCase();
    public java.lang.String toUpperCase(java.util.Locale);
    public java.lang.String trim();
}

# Remove - StringBuffer method calls. Remove all invocations of
# StringBuffer methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.StringBuffer {
    public <init>();
    public <init>(int);
    public <init>(java.lang.String);
    public <init>(java.lang.CharSequence);
    public java.lang.String toString();
    public char charAt(int);
    public int capacity();
    public int codePointAt(int);
    public int codePointBefore(int);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
}

# Remove - StringBuilder method calls. Remove all invocations of
# StringBuilder methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.StringBuilder {
    public <init>();
    public <init>(int);
    public <init>(java.lang.String);
    public <init>(java.lang.CharSequence);
    public java.lang.String toString();
    public char charAt(int);
    public int capacity();
    public int codePointAt(int);
    public int codePointBefore(int);
    public int indexOf(java.lang.String,int);
    public int lastIndexOf(java.lang.String);
    public int lastIndexOf(java.lang.String,int);
    public int length();
    public java.lang.String substring(int);
    public java.lang.String substring(int,int);
}

# Remove - System method calls. Remove all invocations of System
# methods without side effects whose return values are not used.
-assumenosideeffects public class java.lang.System {
    public static native long currentTimeMillis();
    static java.lang.Class getCallerClass();
    public static native int identityHashCode(java.lang.Object);
    public static java.lang.SecurityManager getSecurityManager();
    public static java.util.Properties getProperties();
    public static java.lang.String getProperty(java.lang.String);
    public static java.lang.String getenv(java.lang.String);
    public static native java.lang.String mapLibraryName(java.lang.String);
    public static java.lang.String getProperty(java.lang.String,java.lang.String);
}
