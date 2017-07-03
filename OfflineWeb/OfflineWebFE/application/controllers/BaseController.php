<?php

require_once 'Utility.php';

class BaseController extends Zend_Controller_Action {
    
    private static $logger = NULL;

    public function init() {
        if (self::$logger === NULL) {
            self::$logger = Utility::getLogger();
        }
    }
    
    public function preDispatch() {
        
        /** get the user agent */
        $bootstrap = $this->getInvokeArg('bootstrap');
        $userAgent = $bootstrap->getResource('useragent');
        
        /** call to initialize browser type */
        $userAgent->getDevice();

        /* if desktop do nothing, so use standard layout file names */
        if ($userAgent->getBrowserType() === 'desktop') {
            return null;
        }
 
        /** @var $renderer Zend_Controller_Action_Helper_ViewRenderer */
        $renderer = $this->_helper->viewRenderer;
        $renderer->setViewScriptPathNoControllerSpec(':action.:device.:suffix');
        $renderer->setViewScriptPathSpec(':controller/:action.:device.:suffix');
 
        switch ($userAgent->getBrowserType()) {
            default:
            case 'mobile':
                $renderer->getInflector()->setStaticRule('device', 'mobile');
                break;
        }
    }
}