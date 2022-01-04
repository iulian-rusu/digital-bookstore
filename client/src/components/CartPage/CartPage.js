import React, { Component } from 'react'
import CartOrder from '../CartOrder/CartOrder'
import './CartPage.css'

export default class CartPage extends Component {
    constructor(props) {
        super(props)

        this.state = {
            error: ""
        }

        this.finalizeOrder = async () => {
            if (this.props.currentOrder.length == 0) {
                this.setState({
                    error: `Your cart is empty!`
                })
                return
            }

            const uri = `http://localhost:8081/api/orders/client/${this.props.user.userId}`
            const orderBody = {
                items: this.props.currentOrder
            }
            const res = await fetch(uri, {
                method: 'post',
                headers: {
                    'Authorization': `Bearer ${this.props.user.token}`,
                    'Content-type': 'application/json'
                },
                body: JSON.stringify(orderBody)
            })
            if (!res.ok) {
                this.setState({
                    error: `Cannot complete order: response status ${res.status}`
                })
                return
            }
            this.props.clearCurrentOrder()
            this.setState({
                error: `Order completed`
            })
        }
    }

    render() {
        return (
            <div className='CartPage flexColumn alignCenter'>
                <div id="cartOptions">
                    <button className='brightButton' onClick={this.finalizeOrder}>Finalize Order</button>
                </div>
                <CartOrder currentOrder={this.props.currentOrder} removeOrderedItem={this.props.removeOrderedItem} />
                <h3>{this.state.error ? this.state.error : "Your cart"}</h3>
            </div>
        )
    }
}
