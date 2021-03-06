

/**
 * Qi4j WebSite Progressive Enhancement
 */
$( document ).ready( function($){

    /**
     * Glossary.
     */
    function glossary( $section ) {
        
        // Better style
        var $dts = $section.find('dt');
        $dts.css('margin','2em 0 1em 0');
        $dts.css('padding','0 0 0.3em 0.5em');
        $dts.css('border-bottom','1px solid #ddd');
        $dts.css('color','#0B75BC');
        
        // Better behavior
        function highlight( hash ) {
            if ( hash ) {
                var target = $dts.find(hash);
                var dt = target.parent();
                var dd = dt.next();
                console.log(dt);
                console.log(dd);
                // Scroll
                $.scrollTo( dt, 200, { 'offset': -96 });
                // Highlight
                dd.fadeOut(50).fadeIn(200);
            }
        }
        $( window ).bind( 'hashchange', function() {
            highlight( window.location.hash );
        });
        highlight( window.location.hash );
    }

    /**
     * Community/Codebase.
     */
    function codebase( $section ) {
        // Add Github Repositories
        $section.find( 'ul.itemizedlist li' ).each( function( idx, each_li ) {
            var $each_li = $( each_li );
            var each_li_text = $each_li.text().trim();
            switch( each_li_text ) {
                case 'qi4j-sdk':
                case 'qi4j-sandbox':
                    console.log( each_li_text );
                    $each_li.empty();
                    $each_li.css( 'list-style-position', 'inside' );
                    $each_li.html( '<div class="github-widget" data-repo="Qi4j/' + each_li_text + '"></div>' );
                    break;
            }
        } );
    }
    
    /**
     * Community/Mailing List
     */
    function mailing_list( $section ) {
        // Add Nabble forum
        $section.append('<iframe id="qi4j-dev-iframe" class="qi4j-iframe"\
                src="http://qi4j-dev.23929.n6.nabble.com/"\
                frameborder="0" scrolling="no" allowtransparency="true">\
        </iframe>');
    }
    
    /**
     * Community/Issue Tracker
     */
    function issue_tracker( $section ) {
        // Add Jira Roadmap
        var jira_base_url = 'http://team.ops4j.org/';
        var jira_api = jira_base_url + 'rest/api/latest/';
        var jira_url = jira_base_url + 'browse/QI';
                
        $.getJSON( jira_api + 'project/QI?jsonp-callback=?&callback=?', function( data ) {
            if ( data.versions.length > 0 ) {
                var widget = document.createElement( "div" );
                widget.className = "jira-versions-widget";
                var ul = document.createElement( "ul" );
                ul.className = "jira-versions";
                for( var idx_version = data.versions.length - 1; idx_version >= 0; idx_version-- ) {
                    var version = data.versions[ idx_version ];
                    var li = document.createElement( "li" );
                    li.className = 'jira-version';
                                
                    var title = '<div class="title">';
                    if( version.archived || version.released ) {
                        title += '<img src="http://team.ops4j.org/images/icons/package_16.gif" style="float:left"/>';
                    } else {
                        title += '<img src="http://team.ops4j.org/images/icons/box_16.gif" style="float:left"/>';
                    }
                    title += '<p><strong><a href="' + version.self + '">' + version.name + '</a></strong> - ' + version.description + '</p>';
                    title += "</div>";
                                
                    var issues = '<div class="issues" id="' + version.id + '-issues">';
                    issues += "</div>";
                                
                    var links = '<div class="links">';
                    if ( version.archived || version.released ) {
                        links += '<p>Release date: <strong>' + version.userReleaseDate + '</strong></p>';
                    } else {
                        links += '<p>Not released yet.</p>';
                    }
                    links += '<a href="' + version.self + '">Open</a>';
                    links += "</div>";

                    li.innerHTML = title + issues + links;
                    ul.appendChild( li );
                    // Fetch issues count - The Jira rest api does not provide a roadmap view ...
                    var version_url = version.self;
                    $.getJSON( version_url + '/unresolvedIssueCount?jsonp-callback=?&callback=?', function(data) {
                        var $issues = $( '#' + data.self.substring( data.self.lastIndexOf( '/' ) + 1 ) + '-issues' );
                        if (data.issuesUnresolvedCount) {
                            $issues.append('<p>' + data.issuesUnresolvedCount + ' unresolved issue(s).</p>');
                        }
                    });
                    $.getJSON( version_url + '/relatedIssueCounts?jsonp-callback=?&callback=?', function(data) {
                        var $issues = $( '#' + data.self.substring( data.self.lastIndexOf( '/' ) + 1 ) + '-issues' );
                        if(data.issuesFixedCount) {
                            $issues.append('<p>' + data.issuesFixedCount + ' fixed issue(s).</p>');
                        }
                    });
                }
                widget.appendChild( ul );
                $section.append( widget );
            }
        });
    }
    
    /**
     * Community/Continuous Integration.
     */
    function continuous_integration( $section ) {
        // Add Jenkins Jobs
        var ci_url = 'https://qi4j.ci.cloudbees.com/';
        var ci_images_url = ci_url + 'images/16x16/';
        var $target = $section.find('a.ulink[href=' + ci_url + ']');
                
        $.getJSON( ci_url + 'api/json?depth=2&jsonp=?&callback=?', function( data ) {
            if ( data.jobs.length > 0 ) {
                var widget = document.createElement( "div" );
                widget.className = "jenkins-widget";
                var ul = document.createElement( "ul" );
                ul.className = "jenkins-jobs";
                for( var idx_job = 0; idx_job < data.jobs.length; idx_job++ ) {
                    var job = data.jobs[ idx_job ];
                    if ( job.buildable ) {
                        var li = document.createElement( "li" );
                        li.className = 'jenkins-job';
                                
                        var title = '<div class="title">';
                        if (job.color.indexOf('_anime') != -1) {
                            title += '<img src="' + ci_images_url + job.color + '.gif" style="float:left"/>';
                        } else {
                            title += '<img src="' + ci_images_url + job.color + '.png" style="float:left"/>';
                        }
                        title += '<p><strong>' + job.displayName + '</strong></p>';
                        title += "</div>";
                                
                        var health = '<div class="health">';
                        for( var idx_health = 0; idx_health < job.healthReport.length; idx_health++ ) {
                            var health_data = job.healthReport[ idx_health ];
                            health += '<div class="health-item">';
                            health += '<p><img src="' + ci_images_url + health_data.iconUrl + '"/>' + health_data.description + '</p>';
                            health += '</div>';
                        }
                        if( job.lastCompletedBuild.artifacts.length > 0 ) {
                            health += '<p><strong>Build artifacts:</strong></p><ul>';
                            for( var idx_artifacts = 0; idx_artifacts < job.lastCompletedBuild.artifacts.length; idx_artifacts++ ) {
                                var artifact = job.lastCompletedBuild.artifacts[ idx_artifacts ];
                                health += '<li><a href="'+job.lastCompletedBuild.url+'artifact/'+artifact.relativePath+'">'+artifact.fileName+'</a></li>';
                            }
                            health += '</ul>';
                        }
                        health += "</div>";
                                
                        var links = '<div class="links">';
                        var last = new Date(job.lastCompletedBuild.timestamp * 1000);
                        links += '<p> Latest <strong>completed</strong> build <a href="' + job.lastCompletedBuild.url + '" target="_blank">#' + job.lastCompletedBuild.number + '</a> on ' + last + '</p>';
                        links += '<a href="' + job.url + '" target="_blank">Open</a>';
                        links += "</div>";

                        li.innerHTML = title + health + links;
                        ul.appendChild( li );
                    }
                }
                widget.appendChild( ul );
                $target.after( widget );
            }
            
            // Add relationship to builds RSS
            $('<link rel="alternate" type="application/rss+xml" title="Qi4j CI RSS"  href="https://qi4j.ci.cloudbees.com/rssAll" />').appendTo( $('head') );
            
        });
    }

    // Global enhancements
    $("a.ulink[href^='http:']").attr('target','_blank'); // Open external user links in a new window/tab
    $("a.ulink[href^='https:']").attr('target','_blank'); // Open external user links in a new window/tab

    // Section specific enhancements
    var $section = $( 'body > div.section' );
    var section_title = $section.attr( 'title' ).trim();
    switch( section_title ) {
        case "Glossary":
            glossary( $section );
            break;
        case "Codebase":
            codebase( $section );
            break;
        case "Mailing List":
            mailing_list( $section );
            break;
        case "Issue Tracker":
            issue_tracker( $section );
            break;
        case "Continuous Integration":
            continuous_integration( $section );
            break;
        default:
            break;
    }

    // jquery.githubRepoWidget.js from https://github.com/JoelSutherland/GitHub-jQuery-Repo-Widget
    $(function(){
        var i = 0;
        $('.github-widget').each(function(){
            if(i == 0) $('head').append('<style type="text/css">.github-box{font-family:helvetica,arial,sans-serif;font-size:13px;line-height:18px;background:#fafafa;border:1px solid #ddd;color:#666;border-radius:3px}.github-box a{color:#4183c4;border:0;text-decoration:none}.github-box .github-box-title{position:relative;border-bottom:1px solid #ddd;border-radius:3px 3px 0 0;background:#fcfcfc;background:-moz-linear-gradient(#fcfcfc,#ebebeb);background:-webkit-linear-gradient(#fcfcfc,#ebebeb);}.github-box .github-box-title h3{font-family:helvetica,arial,sans-serif;font-weight:normal;font-size:16px;color:gray;margin:0;padding:10px 10px 10px 30px;background:url(https://a248.e.akamai.net/assets.github.com/images/icons/public.png) 7px center no-repeat}.github-box .github-box-title h3 .repo{font-weight:bold}.github-box .github-box-title .github-stats{position:absolute;top:8px;right:10px;background:white;border:1px solid #ddd;border-radius:3px;font-size:11px;font-weight:bold;line-height:21px;height:21px}.github-box .github-box-title .github-stats a{display:inline-block;height:21px;color:#666;padding:0 5px 0 18px;background:url(https://a248.e.akamai.net/assets.github.com/images/modules/pagehead/repostat.png) no-repeat}.github-box .github-box-title .github-stats .watchers{border-right:1px solid #ddd;background-position:3px -2px}.github-box .github-box-title .github-stats .forks{background-position:0 -52px;padding-left:15px}.github-box .github-box-content{padding:10px;font-weight:300}.github-box .github-box-content p{margin:0}.github-box .github-box-content .link{font-weight:bold}.github-box .github-box-download{position:relative;border-top:1px solid #ddd;background:white;border-radius:0 0 3px 3px;padding:10px;height:24px}.github-box .github-box-download .updated{margin:0;font-size:11px;color:#666;line-height:24px;font-weight:300}.github-box .github-box-download .updated strong{font-weight:bold;color:#000}.github-box .github-box-download .download{position:absolute;display:block;top:10px;right:10px;height:24px;line-height:24px;font-size:12px;color:#666;font-weight:bold;text-shadow:0 1px 0 rgba(255,255,255,0.9);padding:0 10px;border:1px solid #ddd;border-bottom-color:#bbb;border-radius:3px;background:#f5f5f5;background:-moz-linear-gradient(#f5f5f5,#e5e5e5);background:-webkit-linear-gradient(#f5f5f5,#e5e5e5);}.github-box .github-box-download .download:hover{color:#527894;border-color:#cfe3ed;border-bottom-color:#9fc7db;background:#f1f7fa;background:-moz-linear-gradient(#f1f7fa,#dbeaf1);background:-webkit-linear-gradient(#f1f7fa,#dbeaf1);</style>');
            i++;
            var $container = $(this);
            var repo = $container.data('repo');
            $.ajax({
                url: 'https://api.github.com/repos/' + repo,
                dataType: 'jsonp',
                success: function(results){
                    var repo = results.data;
                    var date = new Date(repo.pushed_at);
                    var pushed_at = date.getMonth() + '-' + date.getDate() + '-' + date.getFullYear();
                    var $widget = $(' \
					<div class="github-box repo">  \
					    <div class="github-box-title"> \
					        <h3> \
					            <a class="owner" href="' + repo.owner.url.replace('api.','').replace('users/','') + '">' + repo.owner.login  + '</a> \
					            /  \
					            <a class="repo" href="' + repo.url.replace('api.','').replace('repos/','') + '">' + repo.name + '</a> \
					        </h3> \
					        <div class="github-stats"> \
					            <a class="watchers" href="' + repo.url.replace('api.','').replace('repos/','') + '/watchers">' + repo.watchers + '</a> \
					            <a class="forks" href="' + repo.url.replace('api.','').replace('repos/','') + '/forks">' + repo.forks + '</a> \
					        </div> \
					    </div> \
					    <div class="github-box-content"> \
					        <p class="description">' + repo.description + ' &mdash; <a href="' + repo.url.replace('api.','').replace('repos/','') + '#readme">Read More</a></p> \
					        <p class="link"><a href="' + repo.homepage + '">' + repo.homepage + '</a></p> \
					    </div> \
					    <div class="github-box-download"> \
					        <p class="updated">Latest commit to the <strong>develop</strong> branch on ' + pushed_at + '</p> \
					        <a class="download" href="' + repo.url.replace('api.','').replace('repos/','') + '/zipball/develop">Download as zip</a> \
					    </div> \
					</div> \
				');
                    $widget.appendTo($container);
                } 
            })
        });
    });

} );
