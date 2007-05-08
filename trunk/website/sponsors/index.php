<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>PhET Would Like to Thank</h1>

            <div class="cellTwo">
                <h3>Financial Support</h3>

                <ul class="people">
                    <li class="hewlett"><img class="sponsors" src="../images/hewlett-logo.jpg" /><strong><a href="http://www.hewlett.org/Default.htm">The William and Flora Hewlett Foundation</a></strong></li>

                    <li class="nsf"><img class="sponsors" src="../images/nsf-logo.gif" /><strong><a href="http://www.nsf.gov/">The National Science Foundation</a></strong></li>

                    <li class="kavli"><img class="sponsors" src="../images/kavli-logo.jpg" /><strong><a href="http://www.kavlifoundation.org/index.php">The Kavli Operating Institute</a></strong></li>
                </ul><br />

                <ul class="people">
                    <li style="list-style: none">
                        <h3>Technical Support</h3><br />
                    </li>

                    <li class="one"><strong><a href="http://www.cs.umd.edu/hcil/piccolo/index.shtml">Piccolo</a></strong><br />
                    <em>an open source graphics implementation</em></li>

                    <li class="two"><strong><a href="http://www.jfree.org/jfreechart/">JFreeChart</a></strong><br />
                    <em>an open source chart implementation</em></li>

                    <li class="three"><strong><a href="http://rsheh.web.cse.unsw.edu.au/homepage/index.php?id=34">Raymond Sheh's</a></strong><br />
                    <em>dynamics engine JADE</em></li>

                    <li><strong><a href="http://sourceforge.net/">Sourceforge.net</a></strong><br />
                    <em>for hosting our source code repository</em></li>

                    <li><strong><a href="http://proguard.sourceforge.net">Proguard</a></strong><br />
                    <em>an open source tool for code</em></li>

                    <li><strong><a href="http://www.jetbrains.com/idea/">JetBrains</a></strong><br />
                    <em>for providing for our Java development environment</em></li>
                </ul>
            </div>
        <?php
    }

    print_site_page('print_content', 8);

?>