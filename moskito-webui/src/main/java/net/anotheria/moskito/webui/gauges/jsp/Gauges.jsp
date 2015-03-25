<%@ page language="java" contentType="text/html;charset=UTF-8"	session="true"
        %><%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano"
        %><!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/html">

<jsp:include page="../../shared/jsp/Header.jsp" flush="false"/>

<section id="main">
    <div class="content">

<!-- preparing gauges data -->
        var gauges = [];
<ano:iterate name="gauges" type="net.anotheria.moskito.webui.gauges.api.GaugeAO" id="gauge">
    gauges.push({
        "name": '${gauge.name}',
        "caption": '${gauge.caption}',
        "complete": ${gauge.complete},
        "min": ${gauge.min},
        "current": ${gauge.current},
        "max": ${gauge.max}
    });
</ano:iterate>
        <div class="box">
            <div class="box-title">
                <a class="accordion-toggle tooltip-bottom" title="Close/Open" data-toggle="collapse" href="#collapsestatus"><i class="fa fa-caret-right"></i></a>
                <h3 class="pull-left">
                    Gauges
                </h3>
                <div class="box-right-nav">
                    <a href="" class="tooltip-bottom" title="Help"><i class="fa fa-info-circle"></i></a>
                </div>
            </div>
            <div id="collapsestatus" class="box-content accordion-body collapse in">
                <p>nothing yet</p>
            </div>
        </div>


    </div>

    <jsp:include page="../../shared/jsp/Footer.jsp" flush="false"/>


</section>

</body>
</html>


