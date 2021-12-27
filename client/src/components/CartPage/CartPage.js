import React, { Component } from 'react'
import CartOrder from '../CartOrder/CartOrder'
import './CartPage.css'

export default class CartPage extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className='CartPage flexColumn alignCenter'>
                <div id="cartOptions">
                    <button className='brightButton' onClick={this.finalizeOrder}>Finalize Order</button>
                </div>
                <CartOrder currentOrder={this.props.currentOrder} removeOrderedItem={this.props.removeOrderedItem} />
            </div>
        )
    }
}
