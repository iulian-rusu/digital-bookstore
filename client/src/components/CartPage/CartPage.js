import React, { Component } from 'react'
import OrderTable from '../OrderTable/OrderTable'
import './CartPage.css'

export default class CartPage extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className='CartPage flexColumn alignCenter'>
                <div>
                    <button className='brightButton' onClick={this.finalizeOrder}>Finalize Order</button>
                </div>
                <OrderTable currentOrder={this.props.currentOrder} removeOrderedItem={this.props.removeOrderedItem} />
            </div>
        )
    }
}
