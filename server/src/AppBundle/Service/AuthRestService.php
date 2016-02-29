<?php
namespace AppBundle\Service;

use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;

/**
 * アプリから呼ばれてるかチェックする
 */
class AuthRestService
{

    /**
     * @var RequestStack
     */
    private $request;
    /**
     * @var array
     */
    private $params;

    /**
     * XUserAgentAuthorizationService constructor.
     * @param RequestStack $requestStack
     * @param array $params
     */
    public function __construct(RequestStack $requestStack, $params)
    {
        $this->requestStack = $requestStack;
        $this->params = $params;
    }

    public function auth()
    {
        if ($this->requestStack->getCurrentRequest()->headers->get($this->params[0]) !== md5($this->params[1])) {
            throw new AccessDeniedException("invalid request");
        }
    }

}